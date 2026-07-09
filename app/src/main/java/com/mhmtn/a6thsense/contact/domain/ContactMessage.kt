package com.mhmtn.a6thsense.contact.domain

data class ContactMessage(
    val id: String = "",
    val uid: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val subject: MessageSubject = MessageSubject.GENERAL_FEEDBACK,
    val message: String = "",
    val timestamp: Long = 0L,
    val status: MessageStatus = MessageStatus.NEW,
    val platform: String = "android",
    val deviceInfo: String = ""
)
