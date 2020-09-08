package online.litterae.familyorganizer.sqlite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class MyFriend {
    @PrimaryKey
    var userFirebaseKey = ""
    var email = ""
    var name = ""
    var groupFirebaseKey = ""
    var chatMessages = ""
}