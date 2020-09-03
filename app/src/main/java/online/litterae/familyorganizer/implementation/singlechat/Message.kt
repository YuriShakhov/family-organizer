package online.litterae.familyorganizer.implementation.singlechat

import online.litterae.familyorganizer.application.Const.Companion.STATUS_NEW

class Message (
    val text : String,
    val sender : String,
    val date : String,
    val status : String = STATUS_NEW
)