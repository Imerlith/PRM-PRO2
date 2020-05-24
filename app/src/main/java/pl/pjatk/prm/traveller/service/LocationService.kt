package pl.pjatk.prm.traveller.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import pl.pjatk.prm.traveller.Constants
import pl.pjatk.prm.traveller.EditNoteFragment
import pl.pjatk.prm.traveller.PermissionUtil
import pl.pjatk.prm.traveller.dal.DbAccess
import pl.pjatk.prm.traveller.model.Note
import kotlin.concurrent.thread

class LocationService : Service() {
    lateinit var locationClient: FusedLocationProviderClient
    //TODO: take from settings
    var radius = 1

    companion object {
        var currentLocation: Location? = null
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        locationClient = LocationServices.getFusedLocationProviderClient(this)
        val channel = NotificationChannel(
            Constants.LOCATION_SERVICE_CHANNEL_ID,
            Constants.LOCATION_SERVICE_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(
            this,
            Constants.LOCATION_SERVICE_CHANNEL_ID
        )
            .setContentTitle("")
            .setContentText("")
            .build()

        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        getCurrentLocation()
        Log.d("SRV","SERVICE IS RUNNING")
        return START_STICKY
    }

    private fun getCurrentLocation() {
        val locationRequest = LocationRequest()
        locationRequest.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = Constants.LOCATION_REQUEST_INTERVAL
            fastestInterval = Constants.LOCATION_FASTEST_INTERVAL
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            stopSelf()
            return
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.locations?.get(0)?.let {
                    currentLocation = it
                }

                val db = DbAccess.getInstance(this@LocationService).db
                val nearNotes = ArrayList<Note>()
                thread {
                    for (note: Note in db.notes().getAll()) {
                        val imageLocation = Location("Location from db")
                        imageLocation.let {
                            it.latitude = note.latitude
                            it.longitude = note.longtitude
                        }
                        val distance = imageLocation.distanceTo(currentLocation)
                        if (distance/1000 <= radius) {
                            nearNotes.add(note)
                        }
                    }
                    for (noteNearby: Note in nearNotes) {
                        val intent = Intent(this@LocationService, EditNoteFragment::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        val pendingIntent = PendingIntent.getActivity(
                            this@LocationService,
                            0,
                            intent,
                            0
                        )
                        val notificationBuilder = NotificationCompat.Builder(this@LocationService, Constants.LOCATION_SERVICE_CHANNEL_ID)
                            .setContentTitle("Visited location found")
                            .setContentText("YOu have visited this place before")
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                }


                    //TODO: sprawdzanie czy juz wyswietlone i samo wyswietlanie
                }

            }


        }
        locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }
}
