package online.litterae.familyorganizer.sqlite

import androidx.room.Entity
import androidx.room.PrimaryKey
import online.litterae.familyorganizer.implementation.notifications.Notification

@Entity
data class MyNotification (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val type: String,
    val body: String
)