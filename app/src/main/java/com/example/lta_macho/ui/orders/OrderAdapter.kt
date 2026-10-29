package com.example.lta_macho.ui.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lta_macho.R
import com.example.lta_macho.databinding.ItemOrderBinding

class OrderAdapter(
    private val onSendWaClick: (Order) -> Unit,
    private val onItemClick: (Order) -> Unit
) : ListAdapter<Order, OrderAdapter.OrderViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            val context = binding.root.context

            binding.txtTitleOrder.text = "${order.namaPelanggan} (${order.platNomor})"
            binding.txtLayanan.text = "Layanan: ${order.jenisLayanan}"
            binding.txtBiaya.text = "Total: ${order.totalBiaya}"
            binding.txtWa.text = "WA: ${order.noWhatsapp}"

            binding.txtStatusPengerjaan.text = order.statusPengerjaan
            when (order.statusPengerjaan.lowercase()) {
                "selesai" -> {
                    binding.txtStatusPengerjaan.setTextColor(ContextCompat.getColor(context, R.color.status_success))
                    binding.txtStatusPengerjaan.setBackgroundResource(R.drawable.bg_badge_success)
                }
                "proses" -> {
                    binding.txtStatusPengerjaan.setTextColor(ContextCompat.getColor(context, R.color.status_warning))
                    binding.txtStatusPengerjaan.setBackgroundResource(R.drawable.bg_badge_warning)
                }
                else -> {
                    binding.txtStatusPengerjaan.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
                    binding.txtStatusPengerjaan.setBackgroundResource(R.drawable.bg_badge_neutral)
                }
            }

            binding.txtStatusPembayaran.text = order.statusPembayaran
            if (order.statusPembayaran.equals("Lunas", ignoreCase = true)) {
                binding.txtStatusPembayaran.setTextColor(ContextCompat.getColor(context, R.color.status_success))
                binding.txtStatusPembayaran.setBackgroundResource(R.drawable.bg_badge_success)
            } else {
                binding.txtStatusPembayaran.setTextColor(ContextCompat.getColor(context, R.color.status_error))
                binding.txtStatusPembayaran.setBackgroundResource(R.drawable.bg_badge_danger)
            }

            binding.btnKirimWa.setOnClickListener {
                onSendWaClick(order)
            }

            binding.root.setOnClickListener {
                onItemClick(order)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean = oldItem == newItem
    }
}