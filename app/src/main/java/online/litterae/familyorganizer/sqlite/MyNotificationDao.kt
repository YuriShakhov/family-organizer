package online.litterae.familyorganizer.sqlite

import androidx.room.Dao
import androidx.room.Query

@Dao
interface MyNotificationDao {
    @Query("SELECT * FROM MyNotification")
    fun getAllNotifications(): List<MyNotification?>?
}