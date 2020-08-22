package online.litterae.familyorganizer.firebase

class Invitation (val groupFirebaseKey: String,
                  val groupName: String,
                  val invitedEmail: String,
                  val message: String) {

    var senderEmail: String = ""

    fun toMap() : Map<String, String> {
        val result = mutableMapOf<String, String>()
        result["groupFirebaseKey"] = groupFirebaseKey
        result["groupName"] = groupName
        result["invitedEmail"] = invitedEmail
        result["message"] = message
        result["senderEmail"] = senderEmail
        return result
    }
}