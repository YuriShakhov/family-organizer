package online.litterae.familyorganizer.firebase

data class FirebaseGroup (val name: String) {
    fun toMap(): Map<String, String> {
        val map: MutableMap<String, String> = HashMap()
        map.put("name", name)
        return map
    }
}