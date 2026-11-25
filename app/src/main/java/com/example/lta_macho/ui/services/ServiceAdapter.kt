package com.example.lta_macho.ui.services

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lta_macho.databinding.ItemServiceBinding

class ServiceAdapter(
    private val onItemClick: (ServiceItem) -> Unit
) : ListAdapter<ServiceItem, ServiceAdapter.ServiceViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemServiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ServiceViewHolder(private val binding: ItemServiceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ServiceItem) {
            binding.tvServiceName.text = item.name
            binding.tvServicePrice.text = item.price
            binding.tvVehicleType.text = item.vehicleType
            binding.tvEstimatedTime.text = "Estimasi: ${item.estimatedTime}"
            binding.tvServiceDesc.text = item.description

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ServiceItem>() {
        override fun areItemsTheSame(oldItem: ServiceItem, newItem: ServiceItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ServiceItem, newItem: ServiceItem): Boolean =
            oldItem == newItem
    }
}