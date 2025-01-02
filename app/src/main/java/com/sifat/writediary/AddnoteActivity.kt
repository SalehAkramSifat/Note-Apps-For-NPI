package com.sifat.writediary

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sifat.writediary.databinding.ActivityAddnoteBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddnoteActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddnoteBinding
    lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddnoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        lifecycleScope.launch {
            val longText = withContext(Dispatchers.IO) {
                db.getLongText()
            }
            binding.mainBox.setText(longText)
        }

        binding.done.setOnClickListener {
            val title = binding.title.text.toString()
            val content = binding.mainBox.text.toString()

            val note = Note(0, title, content)
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    db.insertNote(note)
                }
                Toast.makeText(this@AddnoteActivity, "Saved", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@AddnoteActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }

        binding.mainBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
            }
        })
        lifecycleScope.launch {
            val longText = withContext(Dispatchers.IO) {
                db.getLongText()
            }
            binding.mainBox.setText(longText)
        }

    }
}
