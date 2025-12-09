package ru.mpei.md.qrscanner.data.repository

import io.reactivex.rxjava3.core.Single
import ru.mpei.md.qrscanner.data.models.AccessDecision
import ru.mpei.md.qrscanner.data.models.AccessTicket
import ru.mpei.md.qrscanner.data.models.Event

interface AccessRepository {
    fun getUserEvents(userId: String): Single<List<Event>>
    fun getEventDetails(eventId: String): Single<Event>
    fun validateAccessTicket(ticket: AccessTicket): Single<AccessDecision>
    fun processAccessDecision(userId: String, eventId: String, allow: Boolean): Single<AccessDecision>
}