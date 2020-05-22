package pl.pjatk.prm.traveller

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.traveller_note_item.view.*
import pl.pjatk.prm.traveller.model.Note
import java.io.File
import java.io.FileInputStream

class TravellerNoteAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val travelerNotes = mutableListOf<Note>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TravellerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.traveller_note_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return travelerNotes.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TravellerViewHolder) {
            holder.bind(travelerNotes[position])
        }
    }

    class TravellerViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.imageView_card
        val note = itemView.textView_card


        fun bind(travellerNote: Note) {
            note.text = travellerNote.content
            val image = File(travellerNote.photoURI)
            val inputStream = FileInputStream(image)
            val imageBitMap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(imageBitMap)
        }
    }
}