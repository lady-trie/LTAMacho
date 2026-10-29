package com.example.lta_macho.ui.orders

import java.io.Serializable

data class Order(
    val id: String,
    val namaPelanggan: String,
    val platNomor: String,
    val noWhatsapp: String,
    val jenisLayanan: String,
    val totalBiaya: String,
    val statusPengerjaan: String,
    val statusPembayaran: String
) : Serializable