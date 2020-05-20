package pl.pjatk.prm.traveller

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity

class PermissionUtil(private val activity: AppCompatActivity) {
    companion object {
        const val PERMISSION_REQUEST_CODE = 12
    }

    val hasBackgroundPermission get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        activity.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    else true

    val hasLocationFinePermission get() =
        activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    fun checkLocationPermissions(requestIfNot: Boolean = true): Boolean {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        else
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        return (hasBackgroundPermission && hasLocationFinePermission).also {
            if (!it) {
                activity.requestPermissions(
                    permissions,
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }
}