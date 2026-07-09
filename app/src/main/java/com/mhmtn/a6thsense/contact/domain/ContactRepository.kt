package com.mhmtn.a6thsense.contact.domain

interface ContactRepository {
    suspend fun sendMessage(message: ContactMessage): Result<Unit>
}