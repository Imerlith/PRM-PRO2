package pl.pjatk.prm.traveller

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import pl.pjatk.prm.traveller.dal.DbAccess
import pl.pjatk.prm.traveller.model.Note
import pl.pjatk.prm.traveller.service.LocationService
import java.io.File
import java.time.Instant
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val db by lazy { DbAccess.getInstance(applicationContext).db }
    private lateinit var photoUri: Uri
    private lateinit var abPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        if (PermissionUtil(this).checkLocationPermissions()) {
            val serviceIntent = Intent(this, LocationService::class.java)
            startForegroundService(serviceIntent)
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


    private fun createImageGallery() {
       Log.d("NMap", getNotesMap().toString())
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
                    photoUri = uri
                    Log.d("URI", "uri is $photoUri")
                }
            }
        }
    }

    private fun createFile(): File {
        val timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())

        return File.createTempFile(
            Constants.FILE_PREFIX + timestamp,
                Constants.FILE_SUFFIX,
                filesDir
        ).also {
            abPath = it.absolutePath
            Log.d("URI", "absolute path is is ${it.absolutePath}")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.IMAGE_CAPTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            nav_host_fragment.findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundleOf(
                "PhotoUri" to photoUri,
                "absolutePath" to abPath
            ))
        }
    }
}
