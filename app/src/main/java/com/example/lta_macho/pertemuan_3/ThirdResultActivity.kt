package com.example.lta_macho.pertemuan_3

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
            title = "Status Transaksi"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val nama = intent.getStringExtra("EXTRA_NAMA") ?: "Tidak Diisi"
        val plat = intent.getStringExtra("EXTRA_PLAT") ?: "Tidak Diisi"
        val layanan = intent.getStringExtra("EXTRA_LAYANAN") ?: "Tidak Diisi"

        binding.txtNamaResult.text = nama
        binding.txtPlatResult.text = plat
        binding.txtLayananResult.text = layanan
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