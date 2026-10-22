package com.example.lta_macho.pertemuan6

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lta_macho.MainActivity
import com.example.lta_macho.databinding.ActivityAuthBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

open class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sessionPref = getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val userPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)

        // Tombol Login
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
        // Ambil data dari SharedPreferences "UserPref"
                    val savedUsername = userPref.getString("username", null)
                    val savedPassword = userPref.getString("password", null)

            // Rule 1: Username == Password
            val isRule1Valid = username.isNotEmpty() && username == password

            // Rule 2: Username == username di SharedPreferences DAN Password == password di SharedPreferences
            val isRule2Valid = !savedUsername.isNullOrEmpty() &&
                    username == savedUsername &&
                    password == savedPassword

            if (isRule1Valid || isRule2Valid) {
                // Simpan status login di user_pref untuk session
                val editor = sessionPref.edit()
                editor.putBoolean("isLogin", true)
                editor.putString("username", username)
                editor.apply()

                Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()

                // Navigasi ke MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Tampilkan pesan error jika login gagal
                MaterialAlertDialogBuilder(this)
                    .setTitle("Gagal Login")
                    .setMessage("Username atau password salah! Silahkan coba lagi.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

        // Navigasi ke Halaman Registrasi
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}