package com.example.ourproject.ui.locations

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ourproject.R
import com.example.ourproject.data.database.WeatherDatabase
import com.example.ourproject.data.entity.LocationEntity
import com.example.ourproject.data.preferences.WeatherPreferences
import com.example.ourproject.data.repository.LocationRepository
import com.example.ourproject.databinding.FragmentLocationsBinding
import com.example.ourproject.ui.current.CurrentWeatherFragment
import com.example.ourproject.util.LocationHelper
import kotlinx.coroutines.flow.catch
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

        try {
            val database = WeatherDatabase.getDatabase(requireContext())
            locationRepository = LocationRepository(database.locationDao())
            weatherPreferences = WeatherPreferences(requireContext())

            adapter = LocationsAdapter { cityName ->
                val fragment = CurrentWeatherFragment().apply {
                    arguments = Bundle().apply {
                        putString("selected_city", cityName)
                    }
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()
            }

            binding.rvLocations.layoutManager = LinearLayoutManager(requireContext())
            binding.rvLocations.adapter = adapter

            loadLocationsFromDatabase()

            binding.btnAddLocation.setOnClickListener {
            }
        } catch (e: Exception) {
            Log.e("LocationsFragment", "Error in onViewCreated", e)
            e.printStackTrace()
        }
    }

    private fun loadLocationsFromDatabase() {
        var isInitialLoad = true
        lifecycleScope.launch {
            try {
                locationRepository.getAllLocations()
                    .catch { e ->
                        Log.e("LocationsFragment", "Error loading locations", e)
                        e.printStackTrace()
                    }
                    .collect { locations ->
                        if (locations.isEmpty() && isInitialLoad) {
                            isInitialLoad = false
                            insertInitialLocations()
                        } else {
                            updateAdapter(locations)
                        }
                    }
            } catch (e: Exception) {
                Log.e("LocationsFragment", "Error in loadLocationsFromDatabase", e)
                e.printStackTrace()
            }
        }
    }

    private fun insertInitialLocations() {
        lifecycleScope.launch {
            try {
                val cities = listOf("San Francisco", "New York", "Los Angeles", "Chicago", "Miami")
                val weatherRepository = com.example.ourproject.data.repository.WeatherRepository()
                
                val locations = cities.mapNotNull { cityName ->
                    try {
                        val coordinates = LocationHelper.getCoordinatesFromCityName(requireContext(), cityName)
                        if (coordinates != null) {
                            val weather = weatherRepository.getCurrentWeather(coordinates.first, coordinates.second)
                            val temp = weather.main.temp.toInt()
                            val condition = weather.weather.firstOrNull()?.main ?: "Unknown"
                            LocationEntity(name = cityName, temp = temp, condition = condition)
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        Log.e("LocationsFragment", "Error loading weather for $cityName", e)
                        null
                    }
                }
                
                if (locations.isNotEmpty()) {
                    locationRepository.insertAllLocations(locations)
                }
            } catch (e: Exception) {
                Log.e("LocationsFragment", "Error inserting initial locations", e)
                e.printStackTrace()
            }
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