package com.example.lta_macho.pertemuan6

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lta_macho.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol Submit / Daftar
        binding.btnSubmit.setOnClickListener {
            val nama = binding.etNama.text.toString().trim()
            val noHp = binding.etNoHp.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // Validasi Input Kosong
            if (nama.isEmpty() || noHp.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kirim data via Intent ke VerificationActivity
            val intent = Intent(this, VerificationActivity::class.java).apply {
                putExtra("nama", nama)
                putExtra("no_hp", noHp)
                putExtra("username", username)
                putExtra("password", password)
            }
            startActivity(intent)
        }

        // Navigasi Kembali ke Login
        binding.tvToLogin.setOnClickListener {
            finish()
        }
    }
}