package online.litterae.familyorganizer.firebase

class Invitation (val groupFirebaseKey: String,
                  val groupName: String,
                  val email: String,
                  val message: String) {
    fun toMap() : Map<String, String> {
        val result = mutableMapOf<String, String>()
        result["groupFirebaseKey"] = groupFirebaseKey
        result["groupName"] = groupName
        result["email"] = email
        result["message"] = message
        return result
    }
}