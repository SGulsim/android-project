package com.example.ourproject.ui.locations

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ourproject.R
import com.example.ourproject.data.database.WeatherDatabase
import com.example.ourproject.data.entity.LocationEntity
import com.example.ourproject.data.preferences.WeatherPreferences
import com.example.ourproject.data.repository.LocationRepository
import com.example.ourproject.data.repository.WeatherRepository
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
    private var allLocations: List<LocationEntity> = emptyList()

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
            weatherPreferences.setTemperatureUnit(com.example.ourproject.data.preferences.TemperatureUnit.CELSIUS)

            adapter = LocationsAdapter { cityName ->
                (activity as? com.example.ourproject.MainActivity)?.let { mainActivity ->
                    mainActivity.selectedCity = cityName
                    mainActivity.loadFragment(
                        com.example.ourproject.ui.current.CurrentWeatherFragment().apply {
                            arguments = Bundle().apply {
                                putString("selected_city", cityName)
                            }
                        }
                    )
                    mainActivity.binding.bottomNavigation.selectedItemId = R.id.nav_current
                }
            }

            binding.rvLocations.layoutManager = LinearLayoutManager(requireContext())
            binding.rvLocations.adapter = adapter

            setupSearchField()
            setupAddLocationButton()

            loadLocationsFromDatabase()
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
                        val hasOldCities = locations.any { 
                            it.name == "San Francisco" || 
                            it.name == "New York" || 
                            it.name == "Los Angeles" || 
                            it.name == "Chicago" || 
                            it.name == "Miami" 
                        }
                        
                        if ((locations.isEmpty() || hasOldCities) && isInitialLoad) {
                            isInitialLoad = false
                            if (hasOldCities) {
                                locationRepository.deleteAllLocations()
                            }
                            insertInitialLocations()
                        } else {
                            allLocations = locations
                            val query = binding.etSearchCity.text?.toString() ?: ""
                            filterLocations(query)
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
                val cities = listOf("Москва", "Санкт-Петербург", "Новосибирск", "Екатеринбург", "Казань")
                val weatherRepository = com.example.ourproject.data.repository.WeatherRepository()
                
                val locations = cities.mapNotNull { cityName ->
                    try {
                        val coordinates = LocationHelper.getCoordinatesFromCityName(requireContext(), cityName)
                        if (coordinates != null) {
                            val weather = weatherRepository.getCurrentWeather(coordinates.first, coordinates.second)
                            val temp = weather.main.temp.toInt()
                            val condition = weather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercaseChar() } 
                                ?: weather.weather.firstOrNull()?.main ?: "Неизвестно"
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

    private fun setupSearchField() {
        binding.etSearchCity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterLocations(s?.toString() ?: "")
            }
        })

        binding.etSearchCity.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.etSearchCity.text?.toString()?.trim()
                if (!query.isNullOrEmpty()) {
                    addLocationFromSearch(query)
                }
                true
            } else {
                false
            }
        }
    }

    private fun setupAddLocationButton() {
        binding.btnAddLocation.setOnClickListener {
            val query = binding.etSearchCity.text?.toString()?.trim()
            if (!query.isNullOrEmpty()) {
                addLocationFromSearch(query)
            }
        }
    }

    private fun filterLocations(query: String) {
        val filtered = if (query.isBlank()) {
            allLocations
        } else {
            val lowerQuery = query.lowercase()
            allLocations.filter { 
                it.name.lowercase().contains(lowerQuery)
            }
        }
        updateAdapter(filtered)
    }

    private fun addLocationFromSearch(cityName: String) {
        lifecycleScope.launch {
            try {
                val existingLocation = allLocations.find { 
                    it.name.equals(cityName, ignoreCase = true) 
                }
                
                if (existingLocation != null) {
                    binding.etSearchCity.text?.clear()
                    return@launch
                }

                val coordinates = LocationHelper.getCoordinatesFromCityName(requireContext(), cityName)
                if (coordinates == null) {
                    Log.e("LocationsFragment", "Could not find coordinates for city: $cityName")
                    return@launch
                }

                val weatherRepository = WeatherRepository()
                val weather = weatherRepository.getCurrentWeather(coordinates.first, coordinates.second)
                val temp = weather.main.temp.toInt()
                val condition = weather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercaseChar() } 
                    ?: weather.weather.firstOrNull()?.main ?: "Неизвестно"
                
                val locationEntity = LocationEntity(name = cityName, temp = temp, condition = condition)
                locationRepository.insertLocation(locationEntity)
                
                binding.etSearchCity.text?.clear()
            } catch (e: Exception) {
                Log.e("LocationsFragment", "Error adding location from search: $cityName", e)
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