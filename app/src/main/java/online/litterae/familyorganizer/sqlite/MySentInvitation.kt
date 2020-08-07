package online.litterae.familyorganizer.sqlite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class MySentInvitation {
    @PrimaryKey
    var invitationFirebaseKey = ""
    var email = ""
    var message = ""
    var groupFirebaseKey = ""
    var groupName = ""
}