package com.example.ladymobile.auth

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ladymobile.data.supabase.client.SupabaseApiClient
import com.example.ladymobile.data.supabase.model.UserModel
import com.example.lta_macho.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val user = UserModel(name = name, email = email, password = password)
                    val response = SupabaseApiClient.apiService.registerUser(user)

                    if (response.isSuccessful) {
                        Log.d("SUPABASE_REGISTER", "Registrasi Berhasil: ${response.code()}")
                        Toast.makeText(this@RegisterActivity, "Registrasi Berhasil", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = "HTTP ${response.code()}: $errorBody"
                        Log.e("SUPABASE_REGISTER", errorMessage)
                        Toast.makeText(this@RegisterActivity, "Registrasi Gagal: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("SUPABASE_REGISTER", "Exception saat registrasi: ${e.message}", e)
                    Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.tvToLogin.setOnClickListener {
            finish()
        }
    }
}