package pl.pjatk.prm.traveller.dal

import android.content.Context
import androidx.room.Room

class DbAccess private constructor(applicationContext: Context) {
    companion object {
        private var dbAccess: DbAccess? = null
        fun getInstance(applicationContext: Context): DbAccess {
            return dbAccess
                ?: DbAccess(applicationContext)
                    .also { dbAccess = it }
        }
    }

    val db by lazy {
        Room.databaseBuilder(applicationContext, NotesDb::class.java, "notes.db")
            .fallbackToDestructiveMigration()
            .build()
    }

}