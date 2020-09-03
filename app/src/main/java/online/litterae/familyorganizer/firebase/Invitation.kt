package online.litterae.familyorganizer.firebase

import online.litterae.familyorganizer.application.Const.Companion.INVITATION_KEY_DEFAULT
import online.litterae.familyorganizer.application.Const.Companion.STATUS_NEW

class Invitation (val groupFirebaseKey: String = "",
                  val groupName: String = "",
                  val invitedEmail: String = "",
                  val message: String = "",
                  var senderEmail: String = "",
                  var status: String = STATUS_NEW,
                  var invitationFirebaseKey: String = INVITATION_KEY_DEFAULT) {

    fun toMap() : Map<String, String> {
        val result = mutableMapOf<String, String>()
        result["groupFirebaseKey"] = groupFirebaseKey
        result["groupName"] = groupName
        result["invitedEmail"] = invitedEmail
        result["message"] = message
        result["senderEmail"] = senderEmail
        result["status"] = status
        result["invitationFirebaseKey"] = invitationFirebaseKey
        return result
    }
}