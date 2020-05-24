package pl.pjatk.prm.traveller

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_edit_note.*
import pl.pjatk.prm.traveller.dal.DbAccess
import pl.pjatk.prm.traveller.model.Note
import pl.pjatk.prm.traveller.service.LocationService
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.thread

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class EditNoteFragment : Fragment() {
    private var isUpdate: Boolean = false
    private lateinit var abPath: String
    private lateinit var oldBitmap: Bitmap

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val absolutePath = arguments?.get("absolutePath") as String
        abPath = absolutePath

        val imageBitMap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, Uri.fromFile(File(absolutePath)))
        editNoteImageView.setImageBitmap(imageBitMap)
        oldBitmap = imageBitMap

        val noteContent = arguments?.getString("noteContent") ?: ""
        NoteContentInput.setText(noteContent)

        isUpdate = arguments?.getBoolean("isUpdate")!!

        saveNoteButton.setOnClickListener{
            if (isUpdate) {
                findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
            } else {
                onSaveClick()
            }
        }
    }

    private fun onSaveClick() {
        val input = NoteContentInput.text.toString()
        val noteContent = if (input.length <= 500) input else input.substring(0, 501)
        val currentLocation = LocationService.currentLocation
        paintLocalisationAndDate(currentLocation)
        val note =
            currentLocation?.latitude?.let {
                Note(
                    0,
                    abPath,
                    noteContent,
                    it,
                    currentLocation.longitude
                )
            }
        thread {
            saveToDb(note)
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    private fun paintLocalisationAndDate(location: Location?) {
        val newBitmap = Bitmap.createBitmap(
            oldBitmap.width,
            oldBitmap.height,
            oldBitmap.config
        )
        val canvas = Canvas(newBitmap)
        val paint = Paint().apply {
            color = Color.RED //TODO:wziaz z settings
            textSize = 12.toFloat()
            style = Paint.Style.FILL
        }
        canvas.drawPaint(paint)
        canvas.drawBitmap(oldBitmap, 0F, 0F, null)
        val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        canvas.drawText(date, 150F, 300F, paint)
        canvas.drawText(location.toString(), 100F, 200F, paint)
        saveNewImage(newBitmap)
    }

    private fun saveNewImage(bitmap: Bitmap) {
        val timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        val updateFile = File.createTempFile(
            "mod_$timestamp"
        ,".jpg"
        , activity?.filesDir
        ).apply {
            val outputStream = FileOutputStream(this)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        }
        abPath = updateFile.absolutePath
    }

    private fun saveToDb(note: Note?) {
        val db = activity?.applicationContext?.let { DbAccess.getInstance(it).db }
        if (db == null || note == null) {
            Toast.makeText(activity, "Error creating photo", Toast.LENGTH_LONG).show()
        }
        else {
            db.notes().insert(note)
        }
    }
}
