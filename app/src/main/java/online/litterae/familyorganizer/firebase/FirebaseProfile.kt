package online.litterae.familyorganizer.firebase

import online.litterae.familyorganizer.application.Const.Companion.KEY_NAME

data class FirebaseProfile (val name: String) {
    fun toMap(): Map<String, String> {
        val map: MutableMap<String, String> = HashMap()
        map[KEY_NAME] = name
        return map
    }
}