package com.sifat.writediary

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(private var notes:MutableList<Note>, context: Context) : RecyclerView.Adapter<NoteAdapter.NoteViewholder>() {

    private val db : DatabaseHelper = DatabaseHelper(context)

    class NoteViewholder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val titleTextView : TextView = itemView.findViewById(R.id.noteTitle)
        val contentTextView : TextView = itemView.findViewById(R.id.noteContent)
        val updateNote : CardView = itemView.findViewById(R.id.cardView)
        val threeDot : ImageView = itemView.findViewById(R.id.threeDot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_design, parent, false)
        return NoteViewholder(view)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: NoteViewholder, position: Int) {
        val note = notes[position]
        holder.titleTextView.text = note.title
        holder.contentTextView.text = note.content

        holder.updateNote.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateActivity::class.java).apply {
                putExtra("note_id", note.id)
            }
            holder.itemView.context.startActivity(intent)
        }
        holder.threeDot.setOnClickListener { view ->
            val popupMenu = PopupMenu(view.context, holder.threeDot)
            popupMenu.inflate(R.menu.three_dot)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_share -> {
                        val shareText = "Title: ${note.title}\n\nContent: ${note.content}"

                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }

                        val chooser = Intent.createChooser(shareIntent, "Share Note via")
                        view.context.startActivity(chooser)
                        true
                    }

                    R.id.menu_delete -> {
                        val builder = AlertDialog.Builder(view.context)
                        builder.setTitle("Delete Note")
                            .setMessage("Are you sure you want to delete this note?")
                            .setPositiveButton("Yes") { dialog, _ ->
                                // Note Delete Logic
                                db.deleteNote(note.id)
                                notes.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, notes.size)
                                Toast.makeText(view.context, "Note Deleted", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            .setNegativeButton("No") { dialog, _ ->
                                dialog.dismiss()
                            }
                        builder.create().show()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    fun refreshData(newNotes: List<Note>) {
        notes = newNotes.toMutableList()
        notifyDataSetChanged()
    }
}