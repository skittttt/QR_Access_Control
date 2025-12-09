package ru.mpei.md.qrscanner.data.models

data class AccessTicket(
    val userId: String,
    val eventId: String,
    val issuedAt: Long = System.currentTimeMillis()
)