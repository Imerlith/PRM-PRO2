package pl.pjatk.prm.traveller

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.FileProvider
import com.google.android.gms.location.*

import kotlinx.android.synthetic.main.activity_main.*
import pl.pjatk.prm.traveller.dal.DbAccess
import pl.pjatk.prm.traveller.model.Note
import java.io.File
import java.time.Instant
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val db by lazy { DbAccess.getInstance(applicationContext).db }
    private val locationManager by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.locations?.get(0)?.let {
                print("latitude = ${it.latitude} and longitude = ${it.longitude}")
            }
        }
    }
    companion object {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        if (PermissionUtil(this).checkLocationPermissions()) {
            requestLocation()
        }

        //laduj poprzednie zdjecia
        thread {
            createImageGallery()
        }

        fab.setOnClickListener {
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
            if (PermissionUtil(this).checkCameraPermission()) {
                takePicture()
            }

        }
    }

    private fun requestLocation() {
        val request = LocationRequest.create().apply {
            interval = 1000
            numUpdates = 1
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationManager.requestLocationUpdates(request, locationCallback, null)
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
            notesByName[note.photoURI] = note
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

    private fun takePicture() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).let { intent ->
            intent.resolveActivity(packageManager)?.also {
                createFile().also {
                    val uri = FileProvider.getUriForFile(
                        this,
                        Constants.AUTHORITY_NAME,
                        it
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(intent, Constants.IMAGE_CAPTURE_REQUEST_CODE)
                }
            }
        }
    }

    private fun createFile(): File {
        val timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())

        return File.createTempFile(
            Constants.FILE_NAME_PREFIX + timestamp,
                Constants.FILE_SUFFIX,
                filesDir
        )
    }
}
