package com.sifat.writediary

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sifat.writediary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: DatabaseHelper
    private lateinit var notesAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        db = DatabaseHelper(this)
        notesAdapter = NoteAdapter(db.getAllnotes(), this)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = notesAdapter

        binding.addNote.setOnClickListener {
            val intent = Intent(this, AddnoteActivity::class.java)
            startActivity(intent)
        }










        //Navigation Start
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            onNavigationItemSelected(menuItem)
            true
        }
    }
    private fun onNavigationItemSelected(item: MenuItem) {
        when (item.itemId) {
            R.id.action_home -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.action_write -> {
                val Write_intent = Intent(this, AddnoteActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(Write_intent)
            }
            R.id.action_settings -> {
                Toast.makeText(this, "Set", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_about -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("About")
                    .setMessage("App Name: NoteApp\nDeveloper: Saleh Akram Sifat\nVersion: 1.0\nCopyright © Saleh Akram Sifat")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                builder.create().show()
                true
            }
            R.id.action_exit -> {
                showExitDialog()
            }
        }
        binding.drawerLayout.closeDrawer(binding.navView)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                // Handle home action
                true
            }
            R.id.action_write -> {
                // Handle settings action
                true
            }
            R.id.action_settings -> {
                // Handle help action
                true
            }
            R.id.action_about -> {
                // Handle help action
                true
            }
            R.id.action_exit -> {

                true
            }
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(binding.navView)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    //Navigation End

    override fun onResume() {
        super.onResume()
        notesAdapter.refreshData(db.getAllnotes())
    }
    private fun showExitDialog() {
        val builder = AlertDialog.Builder(this) // 'this' should be the Activity context
        builder.setTitle("Exit")
            .setMessage("Are you sure you want to exit?")
            .setCancelable(false) // Dialog can't be dismissed by tapping outside
            .setPositiveButton("Yes") { _, _ ->
                finish() // Close the app
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Close the dialog without exiting the app
            }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

}