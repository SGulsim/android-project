package com.example.ourproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ourproject.databinding.ActivityMainBinding
import com.example.ourproject.ui.current.CurrentWeatherFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Загружаем первый фрагмент
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
                    // TODO: Загрузить ForecastFragment
                    true
                }
                R.id.nav_locations -> {
                    // TODO: Загрузить LocationsFragment
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