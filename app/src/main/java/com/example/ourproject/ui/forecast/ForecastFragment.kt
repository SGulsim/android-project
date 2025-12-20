package com.example.ourproject.ui.forecast

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ourproject.R
import com.example.ourproject.data.preferences.WeatherPreferences
import com.example.ourproject.data.repository.WeatherRepository
import com.example.ourproject.databinding.FragmentForecastBinding
import com.example.ourproject.util.DateFormatter
import com.example.ourproject.util.LocationHelper
import com.example.ourproject.util.WeatherIconMapper
import kotlinx.coroutines.launch

class ForecastFragment : Fragment() {

    private var _binding: FragmentForecastBinding? = null
    private val binding get() = _binding!!

    private val adapter = ForecastAdapter()
    private lateinit var weatherPreferences: WeatherPreferences
    private val weatherRepository = WeatherRepository()

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
        val selectedCity = arguments?.getString("selected_city")
        
        lifecycleScope.launch {
            try {
                val coordinates = if (selectedCity != null) {
                    LocationHelper.getCoordinatesFromCityName(requireContext(), selectedCity)
                } else {
                    LocationHelper.getCurrentLocation(requireContext())
                }
                
                if (coordinates != null) {
                    val forecastResponse = weatherRepository.getDailyForecast(coordinates.first, coordinates.second, 16)
                    Log.d("ForecastFragment", "Forecast response: list size = ${forecastResponse.list.size}")
                    if (forecastResponse.list.isNotEmpty()) {
                        updateAdapter(forecastResponse)
                    } else {
                        Log.e("ForecastFragment", "Forecast list is empty")
                    }
                } else {
                    Log.e("ForecastFragment", "Coordinates are null")
                }
            } catch (e: Exception) {
                Log.e("ForecastFragment", "Error loading forecast", e)
                e.printStackTrace()
            }
        }
    }

    private fun updateAdapter(forecastResponse: com.example.ourproject.data.api.model.DailyForecastResponse) {
        val forecastItems = forecastResponse.list.take(7).mapIndexed { index, item ->
            val dayName = if (index == 0) {
                requireContext().getString(R.string.today)
            } else {
                DateFormatter.getDayName(item.dt)
            }
            val date = DateFormatter.formatShortDate(item.dt)
            val weather = item.weather.firstOrNull()
            val iconName = WeatherIconMapper.getIconName(
                weather?.main ?: "",
                weather?.icon ?: ""
            )
            
            ForecastAdapter.ForecastItem(
                day = dayName,
                date = date,
                highTemp = weatherPreferences.convertTemperature(item.temp.max.toInt()),
                lowTemp = weatherPreferences.convertTemperature(item.temp.min.toInt()),
                precipitation = (item.pop * 100).toInt(),
                iconName = iconName
            )
        }
        adapter.submitList(forecastItems)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}