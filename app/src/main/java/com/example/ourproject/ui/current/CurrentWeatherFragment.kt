package com.example.ourproject.ui.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ourproject.R
import com.example.ourproject.data.preferences.WeatherPreferences
import com.example.ourproject.databinding.FragmentCurrentWeatherBinding

class CurrentWeatherFragment : Fragment() {

    private var _binding: FragmentCurrentWeatherBinding? = null
    private val binding get() = _binding!!
    private lateinit var weatherPreferences: WeatherPreferences

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
        setupWeatherData()
    }

    private fun setupWeatherData() {
        val selectedCity = arguments?.getString("selected_city") ?: "San Francisco"
        val tempCelsius = 72
        val highTempCelsius = 75
        val lowTempCelsius = 62
        
        val temp = weatherPreferences.convertTemperature(tempCelsius)
        val highTemp = weatherPreferences.convertTemperature(highTempCelsius)
        val lowTemp = weatherPreferences.convertTemperature(lowTempCelsius)
        val symbol = weatherPreferences.getTemperatureSymbolShort()
        
        binding.apply {
            // Основная информация
            tvLocation.text = selectedCity
            tvDate.text = "Wednesday, Nov 19"
            tvTemperature.text = "$temp$symbol"
            tvCondition.text = "Partly Cloudy"
            tvHighLow.text = "H:$highTemp$symbol L:$lowTemp$symbol"

            // Влажность
            humidityDetail.ivIcon.setImageResource(R.drawable.ic_humidity)
            humidityDetail.tvLabel.text = "Humidity"
            humidityDetail.tvValue.text = "65%"

            // Ветер
            windDetail.ivIcon.setImageResource(R.drawable.ic_wind)
            windDetail.tvLabel.text = "Wind"
            windDetail.tvValue.text = "12 mph"

            // Дождь
            rainDetail.ivIcon.setImageResource(R.drawable.ic_rain)
            rainDetail.tvLabel.text = "Rain"
            rainDetail.tvValue.text = "20%"

            // Видимость
            visibilityDetail.ivIcon.setImageResource(R.drawable.ic_visibility)
            visibilityDetail.tvLabel.text = "Visibility"
            visibilityDetail.tvValue.text = "10 mi"

            // Восход
            sunriseDetail.ivIcon.setImageResource(R.drawable.ic_sunrise)
            sunriseDetail.tvLabel.text = "Sunrise"
            sunriseDetail.tvValue.text = "6:45 AM"

            // Закат
            sunsetDetail.ivIcon.setImageResource(R.drawable.ic_sunset)
            sunsetDetail.tvLabel.text = "Sunset"
            sunsetDetail.tvValue.text = "5:20 PM"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}