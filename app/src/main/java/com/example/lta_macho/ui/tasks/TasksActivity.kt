package com.example.lta_macho.ui.tasks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lta_macho.R
import com.example.lta_macho.data.supabase.client.SupabaseApiClient
import com.example.lta_macho.data.supabase.model.TaskModel
import com.example.lta_macho.databinding.ActivityTasksBinding
import kotlinx.coroutines.launch

class TasksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTasksBinding
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarTasks)
        supportActionBar?.apply {
            title = "Tugas & Agenda (Supabase)"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        setupRecyclerView()

        binding.btnAddTask.setOnClickListener {
            showAddTaskDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onEditClick = { task -> showEditTaskDialog(task) },
            onDeleteClick = { task -> showDeleteTaskDialog(task) }
        )

        binding.rvTasks.apply {
            layoutManager = LinearLayoutManager(this@TasksActivity)
            adapter = taskAdapter
        }
    }

    private fun loadTasks() {
        binding.pbTasks.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = SupabaseApiClient.apiService.getTasks()
                binding.pbTasks.visibility = View.GONE

                if (response.isSuccessful) {
                    val tasksList = response.body().orEmpty()
                    taskAdapter.submitList(tasksList)

                    if (tasksList.isEmpty()) {
                        binding.tvEmptyTasks.visibility = View.VISIBLE
                        binding.rvTasks.visibility = View.GONE
                    } else {
                        binding.tvEmptyTasks.visibility = View.GONE
                        binding.rvTasks.visibility = View.VISIBLE
                    }
                } else {
                    val errorStr = response.errorBody()?.string()
                    Log.e("SUPABASE_TASKS", "Gagal load tasks: $errorStr")
                    Toast.makeText(this@TasksActivity, "Gagal memuat tugas: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.pbTasks.visibility = View.GONE
                Log.e("SUPABASE_TASKS", "Exception load tasks: ${e.message}", e)
                Toast.makeText(this@TasksActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_task, null)
        val etTaskName = dialogView.findViewById<EditText>(R.id.etDialogTaskName)
        val etDeadline = dialogView.findViewById<EditText>(R.id.etDialogTaskDeadline)
        val rgStatus = dialogView.findViewById<RadioGroup>(R.id.rgTaskStatus)

        AlertDialog.Builder(this)
            .setTitle("Tambah Tugas / Agenda Baru")
            .setView(dialogView)
            .setPositiveButton("Simpan") { dialog, _ ->
                val name = etTaskName.text.toString().trim()
                val deadline = etDeadline.text.toString().trim()

                val status = when (rgStatus.checkedRadioButtonId) {
                    R.id.rbSelesai -> "Selesai"
                    R.id.rbProses -> "Proses"
                    else -> "Belum Selesai"
                }

                if (name.isEmpty() || deadline.isEmpty()) {
                    Toast.makeText(this, "Nama tugas dan deadline tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                lifecycleScope.launch {
                    try {
                        val newTask = TaskModel(taskName = name, deadline = deadline, status = status)
                        val response = SupabaseApiClient.apiService.createTask(newTask)

                        if (response.isSuccessful) {
                            Toast.makeText(this@TasksActivity, "Tugas Berhasil Ditambahkan!", Toast.LENGTH_SHORT).show()
                            loadTasks()
                        } else {
                            val err = response.errorBody()?.string()
                            Toast.makeText(this@TasksActivity, "Gagal simpan: $err", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@TasksActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showEditTaskDialog(task: TaskModel) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_task, null)
        val etTaskName = dialogView.findViewById<EditText>(R.id.etDialogTaskName)
        val etDeadline = dialogView.findViewById<EditText>(R.id.etDialogTaskDeadline)
        val rgStatus = dialogView.findViewById<RadioGroup>(R.id.rgTaskStatus)

        etTaskName.setText(task.taskName)
        etDeadline.setText(task.deadline)

        when (task.status.lowercase()) {
            "selesai" -> dialogView.findViewById<RadioButton>(R.id.rbSelesai).isChecked = true
            "proses" -> dialogView.findViewById<RadioButton>(R.id.rbProses).isChecked = true
            else -> dialogView.findViewById<RadioButton>(R.id.rbBelumSelesai).isChecked = true
        }

        AlertDialog.Builder(this)
            .setTitle("Edit Tugas / Agenda")
            .setView(dialogView)
            .setPositiveButton("Update") { dialog, _ ->
                val newName = etTaskName.text.toString().trim()
                val newDeadline = etDeadline.text.toString().trim()
                val newStatus = when (rgStatus.checkedRadioButtonId) {
                    R.id.rbSelesai -> "Selesai"
                    R.id.rbProses -> "Proses"
                    else -> "Belum Selesai"
                }

                if (newName.isEmpty() || newDeadline.isEmpty()) {
                    Toast.makeText(this, "Nama tugas dan deadline tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val taskId = task.id ?: return@setPositiveButton

                lifecycleScope.launch {
                    try {
                        val updateMap = mapOf(
                            "task_name" to newName,
                            "deadline" to newDeadline,
                            "status" to newStatus
                        )
                        val response = SupabaseApiClient.apiService.updateTask("eq.$taskId", updateMap)

                        if (response.isSuccessful) {
                            Toast.makeText(this@TasksActivity, "Tugas Berhasil Diperbarui!", Toast.LENGTH_SHORT).show()
                            loadTasks()
                        } else {
                            val err = response.errorBody()?.string()
                            Toast.makeText(this@TasksActivity, "Gagal update: $err", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@TasksActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showDeleteTaskDialog(task: TaskModel) {
        val taskId = task.id ?: return
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus tugas \"${task.taskName}\"?")
            .setPositiveButton("Hapus") { dialog, _ ->
                lifecycleScope.launch {
                    try {
                        val response = SupabaseApiClient.apiService.deleteTask("eq.$taskId")
                        if (response.isSuccessful) {
                            Toast.makeText(this@TasksActivity, "Tugas Berhasil Dihapus!", Toast.LENGTH_SHORT).show()
                            loadTasks()
                        } else {
                            val err = response.errorBody()?.string()
                            Toast.makeText(this@TasksActivity, "Gagal hapus: $err", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@TasksActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}