package com.example.task_3_greenspot_anuj_gupta_1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.task_3_greenspot_anuj_gupta_1.databinding.ListItemPlantBinding
import java.util.UUID

class PlantHolder(
    private val binding: ListItemPlantBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(plants: Plants, onPlantClicked: (plantsId: UUID) -> Unit) {
        binding.recordTitle.text = plants.title
        binding.placeTitle.text=plants.place
        binding.recordDate.text = plants.date.toString()

        binding.root.setOnClickListener {
            onPlantClicked(plants.id)
        }

        binding.recordSolved.visibility = if (plants.isSolved) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}

class PlantListAdapter(
    private val plants: List<Plants>,
    private val onPlantClicked: (plantsId: UUID) -> Unit
) : RecyclerView.Adapter<PlantHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlantHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemPlantBinding.inflate(inflater, parent, false)
        return PlantHolder(binding)
    }

    override fun onBindViewHolder(holder: PlantHolder, position: Int) {
        val plants = plants[position]
        holder.bind(plants, onPlantClicked)
    }

    override fun getItemCount() = plants.size
}

