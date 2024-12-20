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

        // ডাটাবেস থেকে পুরোনো নোট লোড করে EditText-এ সেট করা
        lifecycleScope.launch {
            val longText = withContext(Dispatchers.IO) {
                db.getLongText() // ডাটাবেস থেকে বড় টেক্সট আনার কাজ
            }
            binding.mainBox.setText(longText) // EditText-এ সেট করা
        }

        // সেভ বাটনে ক্লিক করলে নোট সেভ হবে
        binding.done.setOnClickListener {
            val title = binding.title.text.toString()
            val content = binding.mainBox.text.toString()

            val note = Note(0, title, content)
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    db.insertNote(note) // ডাটাবেসে নোট সেভ করা
                }
                Toast.makeText(this@AddnoteActivity, "Saved", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@AddnoteActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }

        // TextWatcher smooth scrolling বা বড় টেক্সটের performance উন্নত করার জন্য
        binding.mainBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // Smooth scrolling বা অতিরিক্ত প্রসেসিং এড়াতে কাজ করতে চাইলে এখানে যুক্ত করুন
            }
        })
        lifecycleScope.launch {
            val longText = withContext(Dispatchers.IO) {
                db.getLongText() // ডাটাবেস থেকে বড় টেক্সট আনার জন্য
            }
            binding.mainBox.setText(longText) // EditText-এ সেট করা
        }

    }
}
