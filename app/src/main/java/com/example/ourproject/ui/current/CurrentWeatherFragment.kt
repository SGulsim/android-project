package com.example.ourproject.ui.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ourproject.R
import com.example.ourproject.databinding.FragmentCurrentWeatherBinding

class CurrentWeatherFragment : Fragment() {

    private var _binding: FragmentCurrentWeatherBinding? = null
    private val binding get() = _binding!!

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
        setupWeatherData()
    }

    private fun setupWeatherData() {
        binding.apply {
            // Основная информация
            tvLocation.text = "San Francisco"
            tvDate.text = "Wednesday, Nov 19"
            tvTemperature.text = "72°"
            tvCondition.text = "Partly Cloudy"
            tvHighLow.text = "H:75° L:62°"

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