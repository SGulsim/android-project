package com.example.ourproject.locations

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ourproject.locations.BuildConfig
import com.example.ourproject.core.R
import com.example.ourproject.core.databinding.FragmentLocationsBinding
import com.example.ourproject.data.database.WeatherDatabase
import com.example.ourproject.data.entity.LocationEntity
import com.example.ourproject.data.preferences.WeatherPreferences
import com.example.ourproject.data.preferences.TemperatureUnit
import com.example.ourproject.data.repository.LocationRepository
import com.example.ourproject.data.repository.WeatherRepository
import com.example.ourproject.data.util.LocationHelper
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
            val context = requireContext()
            val database = WeatherDatabase.getDatabase(context)
            locationRepository = LocationRepository(database.locationDao())
            weatherPreferences = WeatherPreferences(context)
            weatherPreferences.setTemperatureUnit(TemperatureUnit.CELSIUS)

            adapter = LocationsAdapter { cityName ->
                val bundle = Bundle().apply {
                    putString("selected_city", cityName)
                }
                findNavController().navigate(com.example.ourproject.core.R.id.nav_current, bundle)
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
                        val oldCityNames = listOf(
                            getString(R.string.old_city_san_francisco),
                            getString(R.string.old_city_new_york),
                            getString(R.string.old_city_los_angeles),
                            getString(R.string.old_city_chicago),
                            getString(R.string.old_city_miami)
                        )
                        val hasOldCities = locations.any { it.name in oldCityNames }
                        
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
                val apiKey = BuildConfig.WEATHER_API_KEY
                if (apiKey.isBlank()) {
                    Log.e("LocationsFragment", "API key is empty! Please set WEATHER_API_KEY in local.properties")
                    return@launch
                }
                Log.d("LocationsFragment", "Inserting initial locations with API key: ${apiKey.take(5)}...")
                
                val cities = listOf(
                    getString(R.string.default_city_moscow),
                    getString(R.string.default_city_spb),
                    getString(R.string.default_city_novosibirsk),
                    getString(R.string.default_city_ekaterinburg),
                    getString(R.string.default_city_kazan)
                )
                val defaultCity = getString(R.string.default_city_moscow)
                val weatherRepository = WeatherRepository(apiKey)
                
                val locations = cities.mapNotNull { cityName ->
                    try {
                        Log.d("LocationsFragment", "Loading weather for city: $cityName")
                        val coordinates = LocationHelper.getCoordinatesFromCityName(requireContext(), cityName, defaultCity)
                        if (coordinates != null) {
                            Log.d("LocationsFragment", "Coordinates for $cityName: lat=${coordinates.first}, lon=${coordinates.second}")
                            val weather = weatherRepository.getCurrentWeather(coordinates.first, coordinates.second)
                            val temp = weather.main.temp.toInt()
                            val condition = weather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercaseChar() } 
                                ?: weather.weather.firstOrNull()?.main ?: getString(R.string.unknown)
                            Log.d("LocationsFragment", "Successfully loaded weather for $cityName: temp=$temp, condition=$condition")
                            LocationEntity(name = cityName, temp = temp, condition = condition)
                        } else {
                            Log.e("LocationsFragment", "Could not get coordinates for city: $cityName")
                            null
                        }
                    } catch (e: Exception) {
                        Log.e("LocationsFragment", "Error loading weather for $cityName", e)
                        e.printStackTrace()
                        null
                    }
                }
                
                Log.d("LocationsFragment", "Loaded ${locations.size} locations out of ${cities.size} cities")
                if (locations.isNotEmpty()) {
                    locationRepository.insertAllLocations(locations)
                    Log.d("LocationsFragment", "Successfully inserted ${locations.size} locations to database")
                } else {
                    Log.e("LocationsFragment", "No locations were loaded. Check API key and network connection.")
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
                val apiKey = BuildConfig.WEATHER_API_KEY
                if (apiKey.isBlank()) {
                    Log.e("LocationsFragment", "API key is empty! Please set WEATHER_API_KEY in local.properties")
                    return@launch
                }
                
                val existingLocation = allLocations.find { 
                    it.name.equals(cityName, ignoreCase = true) 
                }
                
                if (existingLocation != null) {
                    Log.d("LocationsFragment", "City $cityName already exists")
                    binding.etSearchCity.text?.clear()
                    return@launch
                }

                Log.d("LocationsFragment", "Adding new location: $cityName")
                val defaultCity = getString(R.string.default_city_moscow)
                val coordinates = LocationHelper.getCoordinatesFromCityName(requireContext(), cityName, defaultCity)
                if (coordinates == null) {
                    Log.e("LocationsFragment", "Could not find coordinates for city: $cityName")
                    return@launch
                }

                Log.d("LocationsFragment", "Coordinates for $cityName: lat=${coordinates.first}, lon=${coordinates.second}")
                val weatherRepository = WeatherRepository(apiKey)
                val weather = weatherRepository.getCurrentWeather(coordinates.first, coordinates.second)
                val temp = weather.main.temp.toInt()
                val condition = weather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercaseChar() } 
                    ?: weather.weather.firstOrNull()?.main ?: getString(R.string.unknown)
                
                Log.d("LocationsFragment", "Successfully loaded weather for $cityName: temp=$temp, condition=$condition")
                val locationEntity = LocationEntity(name = cityName, temp = temp, condition = condition)
                locationRepository.insertLocation(locationEntity)
                Log.d("LocationsFragment", "Successfully added location: $cityName")
                
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

