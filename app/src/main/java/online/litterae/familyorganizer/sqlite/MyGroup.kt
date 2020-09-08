package online.litterae.familyorganizer.sqlite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class MyGroup {
    @PrimaryKey
    var groupFirebaseKey = ""
    var name = ""
    var iAmAdmin = 0
    var myCurrentGroup = 0
    var groupChatMessages = ""
}