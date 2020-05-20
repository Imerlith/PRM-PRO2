package pl.pjatk.prm.traveller

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")
class Note (
    @PrimaryKey(autoGenerate = true) var id: Int,
    var photoName: String,
    var content: String
)