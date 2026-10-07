package com.example.lta_macho.pertemuan_3

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lta_macho.R
import com.example.lta_macho.databinding.ActivityThirdBinding

class ThirdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityThirdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityThirdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Kelola Transaksi"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        binding.btnKirim.setOnClickListener {
            val nama = binding.inputNama.text.toString()
            val plat = binding.inputNoTujuan.text.toString()

            val jenisLayanan = when (binding.rgJenisLayanan.checkedRadioButtonId) {
                R.id.rbCuciMobil -> "Cuci Mobil (Rp 50.000)"
                R.id.rbCuciMotor -> "Cuci Motor (Rp 20.000)"
                R.id.rbCuciKarpet -> "Cuci Karpet (Rp 35.000)"
                else -> "Cuci Mobil (Rp 50.000)"
            }

            if (nama.isEmpty() || plat.isEmpty()) {
                Toast.makeText(this, "Harap lengkapi semua data!", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, ThirdResultActivity::class.java).apply {
                    putExtra("EXTRA_NAMA", nama)
                    putExtra("EXTRA_PLAT", plat)
                    putExtra("EXTRA_LAYANAN", jenisLayanan)
                }
                startActivity(intent)
            }
        }
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