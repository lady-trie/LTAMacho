package com.example.ladymobile.data.supabase.model

import com.google.gson.annotations.SerializedName

data class TaskModel(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("task_name") val taskName: String,
    @SerializedName("deadline") val deadline: String,
    @SerializedName("status") val status: String
)