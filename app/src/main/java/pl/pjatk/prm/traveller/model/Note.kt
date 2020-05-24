package pl.pjatk.prm.traveller.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")
class Note (
    @PrimaryKey(autoGenerate = true) var id: Int,
    var photoURI: String,
    var content: String,
    var latitude: Double,
    var longtitude: Double
) {
    override fun toString(): String {
        return "Note(id=$id, photoURI='$photoURI', content='$content', latitude=$latitude, longtitude=$longtitude)"
    }
}