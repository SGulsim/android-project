package com.example.ourproject.ui.forecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ourproject.data.database.WeatherDatabase
import com.example.ourproject.data.entity.ForecastEntity
import com.example.ourproject.data.preferences.WeatherPreferences
import com.example.ourproject.data.repository.ForecastRepository
import com.example.ourproject.databinding.FragmentForecastBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ForecastFragment : Fragment() {

    private var _binding: FragmentForecastBinding? = null
    private val binding get() = _binding!!

    private val adapter = ForecastAdapter()
    private lateinit var forecastRepository: ForecastRepository
    private lateinit var weatherPreferences: WeatherPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForecastBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация базы данных и репозитория
        val database = WeatherDatabase.getDatabase(requireContext())
        forecastRepository = ForecastRepository(database.forecastDao())
        weatherPreferences = WeatherPreferences(requireContext())

        binding.rvForecast.layoutManager = LinearLayoutManager(requireContext())
        binding.rvForecast.adapter = adapter

        // Загружаем данные из базы данных
        loadForecastsFromDatabase()
    }

    private fun loadForecastsFromDatabase() {
        var isInitialLoad = true
        lifecycleScope.launch {
            forecastRepository.getAllForecasts().collect { forecasts ->
                if (forecasts.isEmpty() && isInitialLoad) {
                    isInitialLoad = false
                    insertInitialForecasts()
                } else {
                    updateAdapter(forecasts)
                }
            }
        }
    }

    private fun insertInitialForecasts() {
        lifecycleScope.launch {
            val initialForecasts = listOf(
                ForecastEntity(day = "Today", date = "Nov 19", highTemp = 75, lowTemp = 62, precipitation = 20, iconName = "cloud"),
                ForecastEntity(day = "Thursday", date = "Nov 20", highTemp = 74, lowTemp = 63, precipitation = 10, iconName = "sun"),
                ForecastEntity(day = "Friday", date = "Nov 21", highTemp = 68, lowTemp = 58, precipitation = 30, iconName = "cloud"),
                ForecastEntity(day = "Saturday", date = "Nov 22", highTemp = 65, lowTemp = 55, precipitation = 70, iconName = "rain"),
                ForecastEntity(day = "Sunday", date = "Nov 23", highTemp = 70, lowTemp = 59, precipitation = 40, iconName = "rain"),
                ForecastEntity(day = "Monday", date = "Nov 24", highTemp = 73, lowTemp = 62, precipitation = 5, iconName = "sun"),
                ForecastEntity(day = "Tuesday", date = "Nov 25", highTemp = 75, lowTemp = 64, precipitation = 0, iconName = "sun")
            )
            forecastRepository.insertAllForecasts(initialForecasts)
        }
    }

    private fun updateAdapter(forecasts: List<ForecastEntity>) {
        val forecastItems = forecasts.map { entity ->
            ForecastAdapter.ForecastItem(
                day = entity.day,
                date = entity.date,
                highTemp = weatherPreferences.convertTemperature(entity.highTemp),
                lowTemp = weatherPreferences.convertTemperature(entity.lowTemp),
                precipitation = entity.precipitation,
                iconName = entity.iconName
            )
        }
        adapter.submitList(forecastItems)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}