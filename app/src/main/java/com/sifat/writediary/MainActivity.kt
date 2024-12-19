package com.sifat.writediary

import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sifat.writediary.databinding.ActivityMainBinding
import java.io.IOException

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
                val writeIntent = Intent(this, AddnoteActivity::class.java)
                startActivity(writeIntent)
            }
            R.id.action_settings -> {
                Toast.makeText(this, "Set", Toast.LENGTH_SHORT).show()
            }
            R.id.action_export -> {
                exportAllNotesToPDF()
            }
            R.id.action_about -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("About")
                    .setMessage("App Name: NoteApp\nDeveloper: Saleh Akram Sifat\nVersion: 1.0\nCopyright © Saleh Akram Sifat")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                builder.create().show()
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
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(binding.navView)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        notesAdapter.refreshData(db.getAllnotes())
    }

    private fun showExitDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exit")
            .setMessage("Are you sure you want to exit?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun exportAllNotesToPDF() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "AllNotesExport.pdf")
        }
        startActivityForResult(intent, REQUEST_CODE_CREATE_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CREATE_FILE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                savePDFToFile(uri)
            } ?: run {
                Toast.makeText(this, "File creation cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun savePDFToFile(uri: Uri) {
        val pdfDocument = PdfDocument()
        val titlePaint = Paint().apply {
            textSize = 18f // টাইটেলের ফন্ট সাইজ
            typeface = Typeface.DEFAULT_BOLD // Bold স্টাইল
        }

        val contentPaint = Paint().apply {
            textSize = 16f // কন্টেন্টের ফন্ট সাইজ
            typeface = Typeface.DEFAULT // Normal স্টাইল
        }
        var yPosition = 100f
        var pageNumber = 1

        var pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        val allNotes = db.getAllnotes() // ডাটাবেস থেকে সমস্ত নোট
        for ((index, note) in allNotes.withIndex()) {
            val title = note.title // নোটের শিরোনাম
            val content = note.content // নোটের বিষয়বস্তু

            if (yPosition > 800f) {
                pdfDocument.finishPage(page)
                pageNumber++
                yPosition = 100f

                pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
            }

            canvas.drawText("$title", 80f, yPosition, titlePaint)
            yPosition += 30f
            canvas.drawText("$content", 80f, yPosition, contentPaint)
            yPosition += 50f
        }
        pdfDocument.finishPage(page)

        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                pdfDocument.writeTo(outputStream)
                Toast.makeText(this, "PDF Saved Successfully!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save PDF", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }



    companion object {
        private const val REQUEST_CODE_CREATE_FILE = 1
    }
}
