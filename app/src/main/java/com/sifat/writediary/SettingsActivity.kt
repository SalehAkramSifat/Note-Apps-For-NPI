package com.sifat.writediary

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sifat.writediary.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences for saving settings
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)

        // Load saved preferences
        val isDarkMode = sharedPreferences.getBoolean("DarkMode", false)
        val fontSize = sharedPreferences.getInt("FontSize", 18)

        // Set initial states
        binding.switchDarkMode.isChecked = isDarkMode
        binding.seekBarFontSize.progress = fontSize

        // Dark Mode toggle listener
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("DarkMode", isChecked)
            editor.apply()
            Toast.makeText(this, "Dark Mode ${if (isChecked) "Enabled" else "Disabled"}", Toast.LENGTH_SHORT).show()
        }

        // Font Size change listener
        binding.seekBarFontSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val editor = sharedPreferences.edit()
                editor.putInt("FontSize", progress)
                editor.apply()
                Toast.makeText(this@SettingsActivity, "Font Size: $progress", Toast.LENGTH_SHORT).show()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Optional: Implement if needed
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Optional: Implement if needed
            }
        })

        // Clear Data button listener
        binding.btnClearData.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            Toast.makeText(this, "All Data Cleared", Toast.LENGTH_SHORT).show()
        }

        // Logout button listener
        binding.btnLogout.setOnClickListener {
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show()
            // Add your logout logic here
        }
    }
}