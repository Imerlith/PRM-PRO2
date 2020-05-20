package pl.pjatk.prm.traveller

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val db by lazy { DbAccess.getInstance(applicationContext).db }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

       //sprawdz permission na gps i kamere
        if (PermissionUtil(this).checkLocationPermissions()) {
            requestLocation()
        }


        //laduj poprzednie zdjecia
        thread {
            createImageGallery()
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

        }
    }

    private fun requestLocation() {

    }

    private fun createImageGallery() {
        println(getNotesMap())
    }

    private fun getPhotos() {

    }

    private fun getNotesMap() :Map<String, Note> {
        val notesByName = HashMap<String, Note>()
        val notes = db.notes().getAll()
        for (note: Note in notes) {
            notesByName[note.photoName] = note
        }
        return notesByName
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
