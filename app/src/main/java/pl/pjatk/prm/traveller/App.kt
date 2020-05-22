package pl.pjatk.prm.traveller

import android.app.Application
import pl.pjatk.prm.traveller.dal.DbAccess

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        DbAccess.getInstance(applicationContext)
    }

    override fun onTerminate() {
        DbAccess.getInstance(applicationContext).db.close()
        super.onTerminate()
    }
}