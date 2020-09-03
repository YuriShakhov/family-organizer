package online.litterae.familyorganizer.sqlite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class MyReceivedInvitation {
    @PrimaryKey
    var invitationFirebaseKey = ""
    var email = ""
    var message = ""
    var groupFirebaseKey = ""
    var groupName = ""
    var status = ""
}