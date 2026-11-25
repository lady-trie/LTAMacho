package com.example.lta_macho.ui.notes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lta_macho.R
import com.example.lta_macho.data.supabase.client.SupabaseApiClient
import com.example.lta_macho.data.supabase.model.NoteModel
import com.example.lta_macho.databinding.ActivityNotesBinding
import kotlinx.coroutines.launch

class NotesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotesBinding
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarNotes)
        supportActionBar?.apply {
            title = "Catatan Operasional (Supabase)"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        setupRecyclerView()

        binding.btnAddNote.setOnClickListener {
            showAddNoteDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(
            onEditClick = { note -> showEditNoteDialog(note) },
            onDeleteClick = { note -> showDeleteNoteDialog(note) }
        )

        binding.rvNotes.apply {
            layoutManager = LinearLayoutManager(this@NotesActivity)
            adapter = noteAdapter
        }
    }

    private fun loadNotes() {
        binding.pbNotes.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = SupabaseApiClient.apiService.getNotes()
                binding.pbNotes.visibility = View.GONE

                if (response.isSuccessful) {
                    val notesList = response.body().orEmpty()
                    noteAdapter.submitList(notesList)

                    if (notesList.isEmpty()) {
                        binding.tvEmptyNotes.visibility = View.VISIBLE
                        binding.rvNotes.visibility = View.GONE
                    } else {
                        binding.tvEmptyNotes.visibility = View.GONE
                        binding.rvNotes.visibility = View.VISIBLE
                    }
                } else {
                    val errorStr = response.errorBody()?.string()
                    Log.e("SUPABASE_NOTES", "Gagal load notes: $errorStr")
                    Toast.makeText(this@NotesActivity, "Gagal memuat catatan: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.pbNotes.visibility = View.GONE
                Log.e("SUPABASE_NOTES", "Exception load notes: ${e.message}", e)
                Toast.makeText(this@NotesActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddNoteDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_note, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.etDialogNoteTitle)
        val etContent = dialogView.findViewById<EditText>(R.id.etDialogNoteContent)

        AlertDialog.Builder(this)
            .setTitle("Tambah Catatan Baru")
            .setView(dialogView)
            .setPositiveButton("Simpan") { dialog, _ ->
                val title = etTitle.text.toString().trim()
                val content = etContent.text.toString().trim()

                if (title.isEmpty() || content.isEmpty()) {
                    Toast.makeText(this, "Judul dan isi catatan tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                lifecycleScope.launch {
                    try {
                        val newNote = NoteModel(title = title, content = content)
                        val response = SupabaseApiClient.apiService.createNote(newNote)

                        if (response.isSuccessful) {
                            Toast.makeText(this@NotesActivity, "Catatan Berhasil Ditambahkan!", Toast.LENGTH_SHORT).show()
                            loadNotes()
                        } else {
                            val err = response.errorBody()?.string()
                            Toast.makeText(this@NotesActivity, "Gagal simpan: $err", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@NotesActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showEditNoteDialog(note: NoteModel) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_note, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.etDialogNoteTitle)
        val etContent = dialogView.findViewById<EditText>(R.id.etDialogNoteContent)

        etTitle.setText(note.title)
        etContent.setText(note.content)

        AlertDialog.Builder(this)
            .setTitle("Edit Catatan")
            .setView(dialogView)
            .setPositiveButton("Update") { dialog, _ ->
                val newTitle = etTitle.text.toString().trim()
                val newContent = etContent.text.toString().trim()

                if (newTitle.isEmpty() || newContent.isEmpty()) {
                    Toast.makeText(this, "Judul dan isi catatan tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val noteId = note.id ?: return@setPositiveButton

                lifecycleScope.launch {
                    try {
                        val updateMap = mapOf("title" to newTitle, "content" to newContent)
                        val response = SupabaseApiClient.apiService.updateNote("eq.$noteId", updateMap)

                        if (response.isSuccessful) {
                            Toast.makeText(this@NotesActivity, "Catatan Berhasil Diperbarui!", Toast.LENGTH_SHORT).show()
                            loadNotes()
                        } else {
                            val err = response.errorBody()?.string()
                            Toast.makeText(this@NotesActivity, "Gagal update: $err", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@NotesActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showDeleteNoteDialog(note: NoteModel) {
        val noteId = note.id ?: return
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus catatan \"${note.title}\"?")
            .setPositiveButton("Hapus") { dialog, _ ->
                lifecycleScope.launch {
                    try {
                        val response = SupabaseApiClient.apiService.deleteNote("eq.$noteId")
                        if (response.isSuccessful) {
                            Toast.makeText(this@NotesActivity, "Catatan Berhasil Dihapus!", Toast.LENGTH_SHORT).show()
                            loadNotes()
                        } else {
                            val err = response.errorBody()?.string()
                            Toast.makeText(this@NotesActivity, "Gagal hapus: $err", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@NotesActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
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