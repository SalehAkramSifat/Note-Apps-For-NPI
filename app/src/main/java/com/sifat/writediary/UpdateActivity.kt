package com.sifat.writediary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sifat.writediary.databinding.ActivityUpdateBinding

class UpdateActivity : AppCompatActivity() {
    lateinit var binding: ActivityUpdateBinding
    lateinit var db : DatabaseHelper
    private var noteId : Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        noteId = intent.getIntExtra("note_id", -1)
        if (noteId == -1){
            finish()
            return
        }

        val note = db.getNotebyId(noteId)
        binding.updatetitle.setText(note.title)
        binding.updateContent.setText(note.content)

        binding.updatedone.setOnClickListener {
            val title = binding.updatetitle.text.toString()
            val content = binding.updateContent.text.toString()

            val updateNote = Note(noteId, title, content)
            db.updateNote(updateNote)
            finish()
        }

    }
}