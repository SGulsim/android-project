package com.example.ourproject.forecast

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ourproject.BuildConfig
import com.example.ourproject.core.R
import com.example.ourproject.core.databinding.FragmentForecastBinding
import com.example.ourproject.data.preferences.WeatherPreferences
import com.example.ourproject.data.repository.WeatherRepository
import com.example.ourproject.data.util.DateFormatter
import com.example.ourproject.data.util.LocationHelper
import com.example.ourproject.data.util.WeatherIconMapper
import kotlinx.coroutines.launch
import java.util.Calendar

class ForecastFragment : Fragment() {

    private var _binding: FragmentForecastBinding? = null
    private val binding get() = _binding!!

    private val adapter = ForecastAdapter()
    private lateinit var weatherPreferences: WeatherPreferences
    private val weatherRepository = WeatherRepository(BuildConfig.WEATHER_API_KEY)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForecastBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weatherPreferences = WeatherPreferences(requireContext())

        binding.rvForecast.layoutManager = LinearLayoutManager(requireContext())
        binding.rvForecast.adapter = adapter

        loadForecastData()
    }

    private fun loadForecastData() {
        val selectedCity = (activity as? com.example.ourproject.MainActivity)?.selectedCity 
            ?: arguments?.getString("selected_city")
        
        lifecycleScope.launch {
            try {
                val defaultCity = requireContext().getString(R.string.default_city_moscow)
                val coordinates = if (selectedCity != null) {
                    Log.d("ForecastFragment", "Loading forecast for city: $selectedCity")
                    LocationHelper.getCoordinatesFromCityName(requireContext(), selectedCity, defaultCity)
                } else {
                    Log.d("ForecastFragment", "Loading forecast for current location")
                    LocationHelper.getCurrentLocation(requireContext(), defaultCity)
                }
                
                if (coordinates != null) {
                    Log.d("ForecastFragment", "Coordinates: lat=${coordinates.first}, lon=${coordinates.second}")
                    val forecastResponse = weatherRepository.getForecast(coordinates.first, coordinates.second)
                    Log.d("ForecastFragment", "Forecast response received: cod=${forecastResponse.cod}, list size = ${forecastResponse.list.size}")
                    if (forecastResponse.list.isNotEmpty()) {
                        updateAdapter(forecastResponse)
                    } else {
                        Log.e("ForecastFragment", "Forecast list is empty")
                    }
                } else {
                    Log.e("ForecastFragment", "Coordinates are null, using default location")
                    val defaultCoords = LocationHelper.getCoordinatesFromCityName(requireContext(), defaultCity, defaultCity)
                    if (defaultCoords != null) {
                        val forecastResponse = weatherRepository.getForecast(defaultCoords.first, defaultCoords.second)
                        if (forecastResponse.list.isNotEmpty()) {
                            updateAdapter(forecastResponse)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ForecastFragment", "Error loading forecast", e)
                e.printStackTrace()
                try {
                    val defaultCity = requireContext().getString(R.string.default_city_moscow)
                    val defaultCoords = LocationHelper.getCoordinatesFromCityName(requireContext(), defaultCity, defaultCity)
                    if (defaultCoords != null) {
                        val forecastResponse = weatherRepository.getForecast(defaultCoords.first, defaultCoords.second)
                        if (forecastResponse.list.isNotEmpty()) {
                            updateAdapter(forecastResponse)
                        }
                    }
                } catch (e2: Exception) {
                    Log.e("ForecastFragment", "Error loading default forecast", e2)
                }
            }
        }
    }

    private fun updateAdapter(forecastResponse: com.example.ourproject.data.api.model.ForecastResponse) {
        val dailyForecasts = groupForecastByDays(forecastResponse.list)
        
        val forecastItems = dailyForecasts.take(7).mapIndexed { index, dayForecast ->
            val dayName = if (index == 0) {
                requireContext().getString(R.string.today)
            } else {
                DateFormatter.getDayName(dayForecast.dateTimestamp)
            }
            val date = DateFormatter.formatShortDate(dayForecast.dateTimestamp)
            val iconName = WeatherIconMapper.getIconName(
                dayForecast.weatherMain ?: "",
                dayForecast.weatherIcon ?: ""
            )
            
            ForecastAdapter.ForecastItem(
                day = dayName,
                date = date,
                highTemp = weatherPreferences.convertTemperature(dayForecast.maxTemp.toInt()),
                lowTemp = weatherPreferences.convertTemperature(dayForecast.minTemp.toInt()),
                precipitation = dayForecast.maxPrecipitation,
                iconName = iconName
            )
        }
        adapter.submitList(forecastItems)
    }
    
    private data class DayForecast(
        val dateTimestamp: Long,
        val minTemp: Double,
        val maxTemp: Double,
        val maxPrecipitation: Int,
        val weatherMain: String?,
        val weatherIcon: String?
    )
    
    private fun groupForecastByDays(forecastList: List<com.example.ourproject.data.api.model.ForecastItem>): List<DayForecast> {
        val calendar = Calendar.getInstance()
        val groupedByDay = mutableMapOf<String, MutableList<com.example.ourproject.data.api.model.ForecastItem>>()
        
        forecastList.forEach { item ->
            calendar.timeInMillis = item.dt * 1000
            val dayKey = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)}-${calendar.get(Calendar.DAY_OF_MONTH)}"
            
            if (!groupedByDay.containsKey(dayKey)) {
                groupedByDay[dayKey] = mutableListOf()
            }
            groupedByDay[dayKey]?.add(item)
        }
        
        return groupedByDay.values.map { dayItems ->
            val minTemp = dayItems.minOfOrNull { it.main.tempMin } ?: 0.0
            val maxTemp = dayItems.maxOfOrNull { it.main.tempMax } ?: 0.0
            val maxPrecipitation = (dayItems.maxOfOrNull { it.pop } ?: 0.0) * 100
            val dayTimeItem = dayItems.firstOrNull { it.sys.pod == "d" } ?: dayItems.first()
            val weather = dayTimeItem.weather.firstOrNull()
            
            DayForecast(
                dateTimestamp = dayItems.first().dt,
                minTemp = minTemp,
                maxTemp = maxTemp,
                maxPrecipitation = maxPrecipitation.toInt(),
                weatherMain = weather?.main,
                weatherIcon = weather?.icon
            )
        }.sortedBy { it.dateTimestamp }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

