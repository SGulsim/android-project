package com.example.ourproject.ui.locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.ourproject.R
import com.example.ourproject.data.database.WeatherDatabase
import com.example.ourproject.data.entity.LocationEntity
import com.example.ourproject.data.preferences.WeatherPreferences
import com.example.ourproject.data.repository.LocationRepository
import com.example.ourproject.databinding.FragmentLocationsBinding
import com.example.ourproject.ui.current.CurrentWeatherFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LocationsFragment : Fragment() {

    private var _binding: FragmentLocationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: LocationsAdapter
    private lateinit var locationRepository: LocationRepository
    private lateinit var weatherPreferences: WeatherPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация базы данных и репозитория
        val database = WeatherDatabase.getDatabase(requireContext())
        locationRepository = LocationRepository(database.locationDao())
        weatherPreferences = WeatherPreferences(requireContext())

        adapter = LocationsAdapter { cityName ->
            // Передаём выбранный город в CurrentWeatherFragment
            val fragment = CurrentWeatherFragment().apply {
                arguments = Bundle().apply {
                    putString("selected_city", cityName)
                }
            }

            // Заменяем фрагмент через MainActivity (как у тебя уже работает)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }

        binding.rvLocations.adapter = adapter

        // Загружаем данные из базы данных
        loadLocationsFromDatabase()

        binding.btnAddLocation.setOnClickListener {
            // Пока ничего не делаем
        }
    }

    private fun loadLocationsFromDatabase() {
        var isInitialLoad = true
        lifecycleScope.launch {
            locationRepository.getAllLocations().collect { locations ->
                if (locations.isEmpty() && isInitialLoad) {
                    isInitialLoad = false
                    insertInitialLocations()
                } else {
                    updateAdapter(locations)
                }
            }
        }
    }

    private fun insertInitialLocations() {
        lifecycleScope.launch {
            val initialLocations = listOf(
                LocationEntity(name = "San Francisco", temp = 72, condition = "Partly Cloudy"),
                LocationEntity(name = "New York", temp = 58, condition = "Rainy"),
                LocationEntity(name = "Los Angeles", temp = 78, condition = "Sunny"),
                LocationEntity(name = "Chicago", temp = 52, condition = "Cloudy"),
                LocationEntity(name = "Miami", temp = 82, condition = "Sunny")
            )
            locationRepository.insertAllLocations(initialLocations)
        }
    }

    private fun updateAdapter(locations: List<LocationEntity>) {
        val locationItems = locations.map { entity ->
            LocationsAdapter.Location(
                name = entity.name,
                temp = weatherPreferences.convertTemperature(entity.temp),
                condition = entity.condition
            )
        }
        adapter.submitList(locationItems)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}