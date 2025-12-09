package ru.mpei.md.qrscanner.data.models

data class Event(
    val eventId: String,
    val title: String,
    val dateTime: String,
    val location: String,
    val description: String
)