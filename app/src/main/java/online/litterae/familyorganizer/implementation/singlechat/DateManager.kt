package online.litterae.familyorganizer.implementation.singlechat

import java.text.SimpleDateFormat
import java.util.*

class DateManager {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun formatDate(date: Date): String {
        val formatedDate = dateFormat.format(date).toString()
        return formatedDate
    }
}