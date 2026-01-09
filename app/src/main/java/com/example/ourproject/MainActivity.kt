package com.example.ourproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.ourproject.core.databinding.ActivityMainBinding
import com.example.ourproject.core.util.ImageLoaderWrapper
import com.example.ourproject.data.preferences.WeatherPreferences

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    var selectedCity: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ImageLoaderWrapper.init(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(com.example.ourproject.core.R.id.nav_host_fragment) as? NavHostFragment
        if (navHostFragment != null) {
            navController = navHostFragment.navController
            binding.bottomNavigation.setupWithNavController(navController)
        }
    }

    fun navigateToCurrentWeather(cityName: String?) {
        if (::navController.isInitialized) {
            selectedCity = cityName
            // Save to preferences
            val prefs = WeatherPreferences(this)
            prefs.setSelectedCity(cityName)
            val bundle = Bundle().apply {
                putString("selected_city", cityName)
            }
            navController.navigate(com.example.ourproject.core.R.id.nav_current, bundle)
        }
    }

    fun navigateToForecast(cityName: String?) {
        if (::navController.isInitialized) {
            selectedCity = cityName
            // Save to preferences
            val prefs = WeatherPreferences(this)
            prefs.setSelectedCity(cityName)
            val bundle = Bundle().apply {
                putString("selected_city", cityName)
            }
            navController.navigate(com.example.ourproject.core.R.id.nav_forecast, bundle)
        }
    }
}