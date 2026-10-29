package com.example.lta_macho.ui.orders

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lta_macho.R
import com.example.lta_macho.databinding.ActivityThirdBinding

class ThirdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityThirdBinding
    private lateinit var orderRepository: OrderRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityThirdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderRepository = OrderRepository(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Kelola Transaksi"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        binding.btnKirim.setOnClickListener {
            val order = createOrderFromInput() ?: return@setOnClickListener

            orderRepository.addOrder(order)
            Toast.makeText(this, "Transaksi Berhasil Disimpan!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, ThirdResultActivity::class.java).apply {
                putExtra("EXTRA_NAMA", order.namaPelanggan)
                putExtra("EXTRA_PLAT", order.platNomor)
                putExtra("EXTRA_NO_HP", order.noWhatsapp)
                putExtra("EXTRA_LAYANAN", order.jenisLayanan)
                putExtra("EXTRA_TOTAL", order.totalBiaya)
                putExtra("EXTRA_STATUS_PENGERJAAN", order.statusPengerjaan)
                putExtra("EXTRA_STATUS_PEMBAYARAN", order.statusPembayaran)
            }
            startActivity(intent)
            finish()
        }

        binding.btnKirimWaDirect.setOnClickListener {
            val order = createOrderFromInput() ?: return@setOnClickListener

            orderRepository.addOrder(order)

            val message = WhatsAppHelper.buildReceiptMessage(order)
            WhatsAppHelper.sendWhatsAppMessage(this, order.noWhatsapp, message)
            finish()
        }
    }

    private fun createOrderFromInput(): Order? {
        val nama = binding.inputNama.text.toString().trim()
        val plat = binding.inputPlat.text.toString().trim()
        val noHp = binding.inputNoHp.text.toString().trim()

        val (jenisLayanan, totalBiaya) = when (binding.rgJenisLayanan.checkedRadioButtonId) {
            R.id.rbCuciMobil -> "Cuci Mobil (Cuci Salju + Vacuum)" to "Rp 50.000"
            R.id.rbCuciMotor -> "Cuci Motor" to "Rp 20.000"
            R.id.rbCuciKarpet -> "Cuci Karpet / Laundry" to "Rp 35.000"
            else -> "Cuci Mobil (Cuci Salju + Vacuum)" to "Rp 50.000"
        }

        val statusPembayaran = when (binding.rgStatusBayar.checkedRadioButtonId) {
            R.id.rbBayarLunas -> "Lunas"
            R.id.rbBelumBayar -> "Belum Bayar"
            else -> "Lunas"
        }

        if (nama.isEmpty() || plat.isEmpty() || noHp.isEmpty()) {
            Toast.makeText(this, "Harap lengkapi Nama, Plat Nomor, dan No. WhatsApp!", Toast.LENGTH_SHORT).show()
            return null
        }

        val orderId = "ORD-${System.currentTimeMillis().toString().takeLast(4)}"
        return Order(
            id = orderId,
            namaPelanggan = nama,
            platNomor = plat,
            noWhatsapp = noHp,
            jenisLayanan = jenisLayanan,
            totalBiaya = totalBiaya,
            statusPengerjaan = "Proses",
            statusPembayaran = statusPembayaran
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}