package com.example.lta_macho.ui.orders

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class OrderRepository(context: Context) {

    private val sharedPref = context.getSharedPreferences("orders_pref", Context.MODE_PRIVATE)

    fun getOrders(): List<Order> {
        val jsonString = sharedPref.getString("orders_list", null)
        if (jsonString.isNullOrEmpty()) {
            val defaultList = createInitialDummyData()
            saveOrders(defaultList)
            return defaultList
        }

        val list = mutableListOf<Order>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    Order(
                        id = obj.optString("id", ""),
                        namaPelanggan = obj.optString("namaPelanggan", ""),
                        platNomor = obj.optString("platNomor", ""),
                        noWhatsapp = obj.optString("noWhatsapp", ""),
                        jenisLayanan = obj.optString("jenisLayanan", ""),
                        totalBiaya = obj.optString("totalBiaya", ""),
                        statusPengerjaan = obj.optString("statusPengerjaan", "Proses"),
                        statusPembayaran = obj.optString("statusPembayaran", "Lunas")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun addOrder(order: Order) {
        val currentOrders = getOrders().toMutableList()
        currentOrders.add(0, order)
        saveOrders(currentOrders)
    }

    fun saveOrders(orders: List<Order>) {
        val jsonArray = JSONArray()
        for (order in orders) {
            val obj = JSONObject().apply {
                put("id", order.id)
                put("namaPelanggan", order.namaPelanggan)
                put("platNomor", order.platNomor)
                put("noWhatsapp", order.noWhatsapp)
                put("jenisLayanan", order.jenisLayanan)
                put("totalBiaya", order.totalBiaya)
                put("statusPengerjaan", order.statusPengerjaan)
                put("statusPembayaran", order.statusPembayaran)
            }
            jsonArray.put(obj)
        }
        sharedPref.edit().putString("orders_list", jsonArray.toString()).apply()
    }

    private fun createInitialDummyData(): List<Order> {
        return listOf(
            Order(
                id = "ORD-001",
                namaPelanggan = "Budi Santoso",
                platNomor = "BM 1234 ABC",
                noWhatsapp = "081234567890",
                jenisLayanan = "Cuci Mobil (Cuci Salju + Vacuum)",
                totalBiaya = "Rp 50.000",
                statusPengerjaan = "Proses",
                statusPembayaran = "Lunas"
            ),
            Order(
                id = "ORD-002",
                namaPelanggan = "Masjid Al-Ikhlas",
                platNomor = "Karpet (3 Pcs)",
                noWhatsapp = "085298765432",
                jenisLayanan = "Cuci Karpet Tebal + Odor Treatment",
                totalBiaya = "Rp 105.000",
                statusPengerjaan = "Belum Dicuci",
                statusPembayaran = "Belum Bayar"
            ),
            Order(
                id = "ORD-003",
                namaPelanggan = "Siti Aminah",
                platNomor = "BM 5678 XYZ",
                noWhatsapp = "081311223344",
                jenisLayanan = "Cuci Motor",
                totalBiaya = "Rp 20.000",
                statusPengerjaan = "Selesai",
                statusPembayaran = "Lunas"
            )
        )
    }
}