package com.sifat.writediary

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sifat.writediary.databinding.ActivityAddnoteBinding

class AddnoteActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddnoteBinding
    lateinit var db : DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddnoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        binding.done.setOnClickListener {
            val title = binding.title.text.toString()
            val content = binding.mainBox.text.toString()

            val note = Note(0, title, content)
            db.insertNote(note)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        }

    }
}