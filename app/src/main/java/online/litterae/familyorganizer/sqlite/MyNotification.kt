package online.litterae.familyorganizer.sqlite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MyNotification (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val type: String,
    val body: String
)