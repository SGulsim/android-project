package com.example.ourproject.ui.forecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ourproject.databinding.FragmentForecastBinding

class ForecastFragment : Fragment() {

    private var _binding: FragmentForecastBinding? = null
    private val binding get() = _binding!!

    private val adapter = ForecastAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForecastBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvForecast.layoutManager = LinearLayoutManager(requireContext())
        binding.rvForecast.adapter = adapter

        // Данные под ТВОЙ текущий ForecastAdapter (который мы сделали 5 минут назад)
        val list = listOf(
            ForecastAdapter.ForecastItem("Today", "Nov 19", 75, 62, 20, "cloud"),
            ForecastAdapter.ForecastItem("Thursday", "Nov 20", 74, 63, 10, "sun"),
            ForecastAdapter.ForecastItem("Friday", "Nov 21", 68, 58, 30, "cloud"),
            ForecastAdapter.ForecastItem("Saturday", "Nov 22", 65, 55, 70, "rain"),
            ForecastAdapter.ForecastItem("Sunday", "Nov 23", 70, 59, 40, "rain"),
            ForecastAdapter.ForecastItem("Monday", "Nov 24", 73, 62, 5, "sun"),
            ForecastAdapter.ForecastItem("Tuesday", "Nov 25", 75, 64, 0, "sun")
        )
        adapter.submitList(list)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}