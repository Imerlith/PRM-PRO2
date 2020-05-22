package pl.pjatk.prm.traveller.dal

import androidx.room.Database
import androidx.room.RoomDatabase
import pl.pjatk.prm.traveller.model.Note

@Database(entities = [Note::class], version = 2)
abstract class NotesDb: RoomDatabase() {
    abstract fun notes() : NoteDao
}