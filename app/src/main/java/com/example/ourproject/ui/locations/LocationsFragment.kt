package com.example.ourproject.ui.locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ourproject.R
import com.example.ourproject.databinding.FragmentLocationsBinding
import com.example.ourproject.ui.current.CurrentWeatherFragment

class LocationsFragment : Fragment() {

    private var _binding: FragmentLocationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: LocationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        val testData = listOf(
            LocationsAdapter.Location("San Francisco", 72, "Partly Cloudy"),
            LocationsAdapter.Location("New York", 58, "Rainy"),
            LocationsAdapter.Location("Los Angeles", 78, "Sunny"),
            LocationsAdapter.Location("Chicago", 52, "Cloudy"),
            LocationsAdapter.Location("Miami", 82, "Sunny")
        )
        adapter.submitList(testData)

        binding.btnAddLocation.setOnClickListener {
            // Пока ничего не делаем
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}