package com.example.ourproject.locations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ourproject.core.databinding.ItemLocationBinding

class LocationsAdapter(
    private val onLocationClick: (String) -> Unit
) : RecyclerView.Adapter<LocationsAdapter.LocationViewHolder>() {

    private val locations = mutableListOf<Location>()

    data class Location(val name: String, val temp: Int, val condition: String)

    fun submitList(newList: List<Location>) {
        locations.clear()
        locations.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = ItemLocationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(locations[position])
    }

    override fun getItemCount() = locations.size

    inner class LocationViewHolder(
        private val binding: ItemLocationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(location: Location) {
            binding.tvCity.text = location.name
            binding.tvCondition.text = location.condition
            binding.tvTemp.text = "${location.temp}°"

            binding.root.setOnClickListener {
                onLocationClick(location.name)
            }
        }
    }
}

