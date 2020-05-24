package pl.pjatk.prm.traveller.dal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import pl.pjatk.prm.traveller.model.Note

@Dao
interface NoteDao {

    @Query("SELECT * FROM note")
    fun getAll(): List<Note>

    @Query("SELECT * FROM note WHERE id = :id")
    fun getById(id: Int): Note?

    @Query("SELECT * FROM note WHERE photoURI = :photoName")
    fun getByPhotoName(photoName: String): Note?

    @Query("SELECT * FROM note WHERE latitude = :latitude AND longtitude = :longitude")
    fun getByLocation(latitude: Double, longitude: Double): List<Note>

    @Insert
    fun insert(note: Note)

    @Update
    fun update(note: Note)
}