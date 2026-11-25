package com.example.lta_macho.data.supabase.api

import com.example.lta_macho.data.supabase.model.NoteModel
import com.example.lta_macho.data.supabase.model.TaskModel
import com.example.lta_macho.data.supabase.model.UserModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApiService {

    // --- USERS ---
    @POST("users")
    suspend fun registerUser(@Body user: UserModel): Response<Unit>

    @GET("users")
    suspend fun loginUser(
        @Query("email") emailFilter: String,
        @Query("password") passwordFilter: String
    ): Response<List<UserModel>>

    @GET("users")
    suspend fun getAllUsers(): Response<List<UserModel>>

    // --- NOTE ---
    @GET("note?select=*&order=id.desc")
    suspend fun getNotes(): Response<List<NoteModel>>

    @POST("note")
    suspend fun createNote(@Body note: NoteModel): Response<Unit>

    @PATCH("note")
    suspend fun updateNote(
        @Query("id") idFilter: String,
        @Body note: Map<String, String>
    ): Response<Unit>

    @DELETE("note")
    suspend fun deleteNote(@Query("id") idFilter: String): Response<Unit>

    // --- TASKS ---
    @GET("tasks?select=*&order=id.desc")
    suspend fun getTasks(): Response<List<TaskModel>>

    @POST("tasks")
    suspend fun createTask(@Body task: TaskModel): Response<Unit>

    @PATCH("tasks")
    suspend fun updateTask(
        @Query("id") idFilter: String,
        @Body task: Map<String, String>
    ): Response<Unit>

    @DELETE("tasks")
    suspend fun deleteTask(@Query("id") idFilter: String): Response<Unit>
}