package pl.pjatk.prm.traveller

class Constants {
    companion object {
        const val IMAGE_CAPTURE_REQUEST_CODE = 5
        const val FILE_PREFIX = "Trav_"
        const val FILE_SUFFIX = ".jpg"
        const val AUTHORITY_NAME = "pl.pjatk.prm.fProvider"
        const val LOCATION_REQUEST_INTERVAL = 10000L
        const val LOCATION_FASTEST_INTERVAL = 2000L
        const val LOCATION_SERVICE_CHANNEL_ID = "location_channel_01"
        const val LOCATION_SERVICE_CHANNEL_NAME = "Traveller location channel"
        const val LOCATION_SERVICE_TAG = "LocationService"
    }
}