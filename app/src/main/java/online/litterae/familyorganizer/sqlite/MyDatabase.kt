package online.litterae.familyorganizer.sqlite

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        MyGroup::class,
        MyFriend::class,
        MySentInvitation::class,
        MyReceivedInvitation::class,
        MyNotification::class],
    version = 1
)
abstract class MyDatabase: RoomDatabase() {
    abstract fun myGroupDao(): MyGroupDao
    abstract fun myFriendDao(): MyFriendDao
    abstract fun myReceivedInvitationDao(): MyReceivedInvitationDao
    abstract fun mySentInvitationDao(): MySentInvitationDao
    abstract fun myNotificationDao(): MyNotificationDao
}