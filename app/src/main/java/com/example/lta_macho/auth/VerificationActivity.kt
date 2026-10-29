package com.example.lta_macho.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lta_macho.databinding.ActivityVerificationBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class VerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nama = intent.getStringExtra("nama") ?: ""
        val noHp = intent.getStringExtra("no_hp") ?: ""
        val username = intent.getStringExtra("username") ?: ""
        val password = intent.getStringExtra("password") ?: ""

        binding.btnVerify.setOnClickListener {
            val otpInput = binding.etOtp.text.toString().trim()

            if (otpInput.isNotEmpty() && otpInput == noHp) {
                val sharedPref = getSharedPreferences("UserPref", MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putString("nama", nama)
                editor.putString("no_hp", noHp)
                editor.putString("username", username)
                editor.putString("password", password)
                editor.apply()

                Toast.makeText(this, "Verifikasi OTP Berhasil! Akun terdaftar.", Toast.LENGTH_SHORT).show()

                val intentLogin = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intentLogin)
                finish()
            } else {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Verifikasi Gagal")
                    .setMessage("Kode OTP salah atau tidak boleh kosong! Kode OTP harus sama dengan No. Handphone registrasi.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }
}