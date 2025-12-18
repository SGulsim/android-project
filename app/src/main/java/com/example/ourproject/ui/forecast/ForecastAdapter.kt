package com.example.ourproject.ui.forecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ourproject.R
import com.example.ourproject.databinding.ItemForecastBinding

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {

    private val items = mutableListOf<ForecastItem>()

    data class ForecastItem(
        val day: String,
        val date: String,
        val highTemp: Int,
        val lowTemp: Int,
        val precipitation: Int,
        val iconName: String = "cloud"
    )

    fun submitList(newItems: List<ForecastItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvDay.text = item.day
            tvDate.text = item.date
            tvHigh.text = "${item.highTemp}°"
            tvLow.text = "${item.lowTemp}°"
            tvPrecip.text = "${item.precipitation}%"

            val iconRes = when (item.iconName) {
                "sun"  -> R.drawable.ic_sunset   // или ic_sunrise, какая красивее
                "rain" -> R.drawable.ic_rain
                else   -> R.drawable.ic_cloud
            }
            ivIcon.setImageResource(iconRes)
        }
    }

    override fun getItemCount() = items.size

    class ViewHolder(val binding: ItemForecastBinding) : RecyclerView.ViewHolder(binding.root)
}