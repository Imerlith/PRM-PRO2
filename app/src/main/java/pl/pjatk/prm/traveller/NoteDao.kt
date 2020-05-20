package pl.pjatk.prm.traveller

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteDao {

    @Query("SELECT * FROM note")
    fun getAll(): List<Note>

    @Query("SELECT * FROM note WHERE id = :id")
    fun getById(id: Int): Note?

    @Query("SELECT * FROM note WHERE photoName = :photoName")
    fun getByPhotoName(photoName: String): Note?

    @Insert
    fun insert(note: Note)

    @Update
    fun update(note: Note)
}