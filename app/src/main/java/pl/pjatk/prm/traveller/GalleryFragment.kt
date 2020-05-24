package pl.pjatk.prm.traveller

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_gallery.*
import pl.pjatk.prm.traveller.dal.DbAccess
import pl.pjatk.prm.traveller.model.Note
import kotlin.concurrent.thread

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class GalleryFragment : Fragment() {

    private val travellerNotesAdapter = TravellerNoteAdapter()
    private val db by lazy { activity?.applicationContext?.let { DbAccess.getInstance(it).db } }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ImageGalery.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = travellerNotesAdapter
        }

        thread {
            val notes = db?.notes()?.getAll()
            activity?.runOnUiThread {
                travellerNotesAdapter.addNotes(notes ?: ArrayList())
            }
        }
    }
}
