package com.example.lta_macho.ui.orders

import android.R
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.lta_macho.databinding.ActivityThirdResultBinding

class ThirdResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityThirdResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityThirdResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Status & Struk Transaksi"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val nama = intent.getStringExtra("EXTRA_NAMA") ?: "Tidak Diisi"
        val plat = intent.getStringExtra("EXTRA_PLAT") ?: "Tidak Diisi"
        val noHp = intent.getStringExtra("EXTRA_NO_HP") ?: "Tidak Diisi"
        val layanan = intent.getStringExtra("EXTRA_LAYANAN") ?: "Tidak Diisi"
        val total = intent.getStringExtra("EXTRA_TOTAL") ?: "-"
        val statusPengerjaan = intent.getStringExtra("EXTRA_STATUS_PENGERJAAN") ?: "Proses"
        val statusPembayaran = intent.getStringExtra("EXTRA_STATUS_PEMBAYARAN") ?: "Lunas"

        binding.txtNamaResult.text = nama
        binding.txtPlatResult.text = plat
        binding.txtNoHpResult.text = noHp
        binding.txtLayananResult.text = layanan
        binding.txtTotalResult.text = total
        binding.txtStatusPengerjaanResult.text = statusPengerjaan
        binding.txtStatusPembayaranResult.text = statusPembayaran

        val order = Order(
            id = "STRUK-${System.currentTimeMillis().toString().takeLast(4)}",
            namaPelanggan = nama,
            platNomor = plat,
            noWhatsapp = noHp,
            jenisLayanan = layanan,
            totalBiaya = total,
            statusPengerjaan = statusPengerjaan,
            statusPembayaran = statusPembayaran
        )

        binding.btnKirimWaResult.setOnClickListener {
            val message = WhatsAppHelper.buildReceiptMessage(order)
            WhatsAppHelper.sendWhatsAppMessage(this, noHp, message)
        }

        binding.btnExportPdf.setOnClickListener {
            PdfExporter.exportOrderToPdf(this, order)
        }

        binding.btnSelesai.setOnClickListener {
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}