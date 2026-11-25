package com.example.lta_macho.data.supabase.model

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)