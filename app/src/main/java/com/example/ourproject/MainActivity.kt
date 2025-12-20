package com.example.ourproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ourproject.databinding.ActivityMainBinding
import com.example.ourproject.ui.current.CurrentWeatherFragment
import com.example.ourproject.ui.locations.LocationsFragment
import com.example.ourproject.ui.forecast.ForecastFragment
import com.example.ourproject.util.ImageLoaderWrapper

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var selectedCity: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        ImageLoaderWrapper.init(this)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            loadFragment(CurrentWeatherFragment())
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_current -> {
                    loadFragment(createCurrentWeatherFragment())
                    true
                }
                R.id.nav_forecast -> {
                    loadFragment(createForecastFragment())
                    true
                }
                R.id.nav_locations -> {
                    loadFragment(LocationsFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun createCurrentWeatherFragment(): Fragment {
        return CurrentWeatherFragment().apply {
            selectedCity?.let {
                arguments = Bundle().apply {
                    putString("selected_city", it)
                }
            }
        }
    }

    private fun createForecastFragment(): Fragment {
        return ForecastFragment().apply {
            selectedCity?.let {
                arguments = Bundle().apply {
                    putString("selected_city", it)
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}