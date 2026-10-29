package com.example.lta_macho.auth

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

        val sessionPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        val userPref = getSharedPreferences("UserPref", MODE_PRIVATE)

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            val savedUsername = userPref.getString("username", null)
            val savedPassword = userPref.getString("password", null)

            val isRule1Valid = username.isNotEmpty() && username == password
            val isRule2Valid = !savedUsername.isNullOrEmpty() &&
                    username == savedUsername &&
                    password == savedPassword

            if (isRule1Valid || isRule2Valid) {
                val editor = sessionPref.edit()
                editor.putBoolean("isLogin", true)
                editor.putString("username", username)
                editor.apply()

                Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Gagal Login")
                    .setMessage("Username atau password salah! Silahkan coba lagi.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}