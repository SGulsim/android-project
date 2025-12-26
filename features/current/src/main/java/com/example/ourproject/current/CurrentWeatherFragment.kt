package com.example.ourproject.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ourproject.current.BuildConfig
import com.example.ourproject.core.R
import com.example.ourproject.core.databinding.FragmentCurrentWeatherBinding
import com.example.ourproject.data.preferences.WeatherPreferences
import com.example.ourproject.data.repository.WeatherRepository
import com.example.ourproject.data.util.DateFormatter
import com.example.ourproject.data.util.LocationHelper
import kotlinx.coroutines.launch

class CurrentWeatherFragment : Fragment() {

    private var _binding: FragmentCurrentWeatherBinding? = null
    private val binding get() = _binding!!
    private lateinit var weatherPreferences: WeatherPreferences
    private val weatherRepository = WeatherRepository(BuildConfig.WEATHER_API_KEY)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherPreferences = WeatherPreferences(requireContext())
        loadWeatherData()
    }

    private fun loadWeatherData() {
        val selectedCity = arguments?.getString("selected_city")
        
        lifecycleScope.launch {
            try {
                val defaultCity = requireContext().getString(R.string.default_city_moscow)
                val coordinates = if (selectedCity != null) {
                    LocationHelper.getCoordinatesFromCityName(requireContext(), selectedCity, defaultCity)
                } else {
                    LocationHelper.getCurrentLocation(requireContext(), defaultCity)
                }
                
                if (coordinates != null) {
                    val weatherResponse = weatherRepository.getCurrentWeather(coordinates.first, coordinates.second)
                    val cityName = selectedCity ?: LocationHelper.getCityNameFromCoordinates(requireContext(), coordinates.first, coordinates.second) 
                        ?: requireContext().getString(R.string.current_location)
                    setupWeatherData(weatherResponse, cityName)
                } else {
                    val defaultCoords = LocationHelper.getCoordinatesFromCityName(requireContext(), defaultCity, defaultCity)
                    if (defaultCoords != null) {
                        val weatherResponse = weatherRepository.getCurrentWeather(defaultCoords.first, defaultCoords.second)
                        setupWeatherData(weatherResponse, defaultCity)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                try {
                    val defaultCity = requireContext().getString(R.string.default_city_moscow)
                    val defaultCoords = LocationHelper.getCoordinatesFromCityName(requireContext(), defaultCity, defaultCity)
                    if (defaultCoords != null) {
                        val weatherResponse = weatherRepository.getCurrentWeather(defaultCoords.first, defaultCoords.second)
                        setupWeatherData(weatherResponse, defaultCity)
                    }
                } catch (e2: Exception) {
                    e2.printStackTrace()
                }
            }
        }
    }

    private fun setupWeatherData(weatherResponse: com.example.ourproject.data.api.model.CurrentWeatherResponse, cityName: String) {
        val tempCelsius = weatherResponse.main.temp.toInt()
        val highTempCelsius = weatherResponse.main.tempMax.toInt()
        val lowTempCelsius = weatherResponse.main.tempMin.toInt()
        
        val temp = weatherPreferences.convertTemperature(tempCelsius)
        val highTemp = weatherPreferences.convertTemperature(highTempCelsius)
        val lowTemp = weatherPreferences.convertTemperature(lowTempCelsius)
        val symbol = weatherPreferences.getTemperatureSymbolShort()
        
        val condition = weatherResponse.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercaseChar() } ?: requireContext().getString(R.string.unknown)
        val humidity = weatherResponse.main.humidity
        val windSpeed = weatherResponse.wind.speed
        val visibility = weatherResponse.visibility / 1000.0
        val rain = weatherResponse.rain?.oneHour ?: 0.0
        val sunrise = DateFormatter.formatTime(weatherResponse.sys.sunrise)
        val sunset = DateFormatter.formatTime(weatherResponse.sys.sunset)
        val date = DateFormatter.formatDate(weatherResponse.dt)
        
        binding.apply {
            tvLocation.text = cityName
            tvDate.text = date
            tvTemperature.text = "$temp$symbol"
            tvCondition.text = condition
            tvHighLow.text = "${requireContext().getString(R.string.high)}:$highTemp$symbol ${requireContext().getString(R.string.low)}:$lowTemp$symbol"

            humidityDetail.ivIcon.setImageResource(R.drawable.ic_humidity)
            humidityDetail.tvLabel.text = requireContext().getString(R.string.humidity)
            humidityDetail.tvValue.text = "$humidity%"

            windDetail.ivIcon.setImageResource(R.drawable.ic_wind)
            windDetail.tvLabel.text = requireContext().getString(R.string.wind)
            windDetail.tvValue.text = "${windSpeed.toInt()} ${requireContext().getString(R.string.m_s)}"

            rainDetail.ivIcon.setImageResource(R.drawable.ic_rain)
            rainDetail.tvLabel.text = requireContext().getString(R.string.rain)
            rainDetail.tvValue.text = "${(rain * 100).toInt()}%"

            visibilityDetail.ivIcon.setImageResource(R.drawable.ic_visibility)
            visibilityDetail.tvLabel.text = requireContext().getString(R.string.visibility)
            visibilityDetail.tvValue.text = "${visibility.toInt()} ${requireContext().getString(R.string.km)}"

            sunriseDetail.ivIcon.setImageResource(R.drawable.ic_sunrise)
            sunriseDetail.tvLabel.text = requireContext().getString(R.string.sunrise)
            sunriseDetail.tvValue.text = sunrise

            sunsetDetail.ivIcon.setImageResource(R.drawable.ic_sunset)
            sunsetDetail.tvLabel.text = requireContext().getString(R.string.sunset)
            sunsetDetail.tvValue.text = sunset
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

