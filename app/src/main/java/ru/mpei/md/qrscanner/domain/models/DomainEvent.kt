package ru.mpei.md.qrscanner.domain.models

data class DomainEvent(
    val eventId: String,
    val title: String,
    val dateTime: String,
    val location: String,
    val description: String
)