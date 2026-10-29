package com.example.lta_macho.ui.orders

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.net.URLEncoder

object WhatsAppHelper {

    fun formatPhoneNumber(phone: String): String {
        var cleaned = phone.replace(Regex("[^0-9]"), "")
        if (cleaned.startsWith("0")) {
            cleaned = "62" + cleaned.substring(1)
        } else if (cleaned.startsWith("8")) {
            cleaned = "62" + cleaned
        }
        return cleaned
    }

    fun buildReceiptMessage(order: Order): String {
        return """
*WASHTRACK - STRUK TRANSAKSI DIGITAL*
----------------------------------------
🆔 *No. Transaksi:* ${order.id}
👤 *Nama Pelanggan:* ${order.namaPelanggan}
🚘 *Plat/Identitas:* ${order.platNomor}
📱 *No. WhatsApp:* ${order.noWhatsapp}
🧺 *Layanan:* ${order.jenisLayanan}
💵 *Total Biaya:* ${order.totalBiaya}
💳 *Status Pembayaran:* ${order.statusPembayaran}
⏳ *Status Pengerjaan:* ${order.statusPengerjaan}
----------------------------------------
Terima kasih telah memilih WASHTRACK!
Solusi Praktis Antrean Cucian Anda.
        """.trimIndent()
    }

    fun sendWhatsAppMessage(context: Context, phone: String, messageText: String) {
        val formattedPhone = formatPhoneNumber(phone)
        if (formattedPhone.isEmpty()) {
            Toast.makeText(context, "Nomor WhatsApp tidak valid!", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val encodedMessage = URLEncoder.encode(messageText, "UTF-8")
            val url = "https://api.whatsapp.com/send?phone=$formattedPhone&text=$encodedMessage"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal membuka WhatsApp: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}