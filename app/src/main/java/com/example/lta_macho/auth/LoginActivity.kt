package com.example.lta_macho.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.lta_macho.MainActivity
import com.example.lta_macho.data.supabase.client.SupabaseApiClient
import com.example.lta_macho.databinding.ActivityAuthBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

open class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sessionPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        val userPref = getSharedPreferences("UserPref", MODE_PRIVATE)

        binding.btnLogin.setOnClickListener {
            val inputUserOrEmail = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (inputUserOrEmail.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap isi email/username dan password!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    // Cek kueri ke Supabase tabel users
                    val emailQuery = "eq.$inputUserOrEmail"
                    val passwordQuery = "eq.$password"
                    val response = SupabaseApiClient.apiService.loginUser(emailQuery, passwordQuery)

                    if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                        val matchedUser = response.body()!!.first()
                        val userName = matchedUser.name.ifEmpty { matchedUser.email }

                        val editor = sessionPref.edit()
                        editor.putBoolean("isLogin", true)
                        editor.putString("username", userName)
                        editor.apply()

                        Toast.makeText(this@LoginActivity, "Login Berhasil! Selamat Datang $userName", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        return@launch
                    }

                    // Fallback cek kueri lokal jika offline / rule bawaan
                    val savedUsername = userPref.getString("username", null)
                    val savedPassword = userPref.getString("password", null)

                    val isRule1Valid = inputUserOrEmail == password
                    val isRule2Valid = !savedUsername.isNullOrEmpty() &&
                            inputUserOrEmail == savedUsername &&
                            password == savedPassword

                    if (isRule1Valid || isRule2Valid) {
                        val editor = sessionPref.edit()
                        editor.putBoolean("isLogin", true)
                        editor.putString("username", inputUserOrEmail)
                        editor.apply()

                        Toast.makeText(this@LoginActivity, "Login Berhasil!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        MaterialAlertDialogBuilder(this@LoginActivity)
                            .setTitle("Gagal Login")
                            .setMessage("Email/Username atau password salah! Silahkan periksa akun Anda di Supabase.")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                } catch (e: Exception) {
                    Log.e("SUPABASE_LOGIN", "Error login: ${e.message}", e)
                    MaterialAlertDialogBuilder(this@LoginActivity)
                        .setTitle("Gagal Login")
                        .setMessage("Tidak dapat terhubung: ${e.localizedMessage}")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}