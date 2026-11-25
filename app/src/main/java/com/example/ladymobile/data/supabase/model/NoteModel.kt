package com.example.ladymobile.data.supabase.model

import com.google.gson.annotations.SerializedName

data class NoteModel(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String
)