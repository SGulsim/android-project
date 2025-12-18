package com.example.ourproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ourproject.databinding.ActivityMainBinding
import com.example.ourproject.ui.current.CurrentWeatherFragment
import com.example.ourproject.ui.locations.LocationsFragment
import com.example.ourproject.ui.forecast.ForecastFragment   // ← добавь этот импорт!

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    loadFragment(CurrentWeatherFragment())
                    true
                }
                R.id.nav_forecast -> {
                    loadFragment(ForecastFragment())   // ← вот сюда!
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

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}