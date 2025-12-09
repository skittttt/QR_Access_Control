package ru.mpei.md.qrscanner.data.repository

import io.reactivex.rxjava3.core.Single
import ru.mpei.md.qrscanner.data.models.AccessDecision
import ru.mpei.md.qrscanner.data.models.AccessStatus
import ru.mpei.md.qrscanner.data.models.DecisionResult
import ru.mpei.md.qrscanner.data.models.Event
import ru.mpei.md.qrscanner.data.models.AccessTicket
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class MockAccessRepository : AccessRepository {
    
    // Mock data storage
    private val events = listOf(
        Event(
            eventId = "E20241001",
            title = "Tech Conference 2024",
            dateTime = "2024-10-01 10:00",
            location = "Main Hall A",
            description = "Annual technology conference with industry leaders"
        ),
        Event(
            eventId = "E20241002",
            title = "Workshop: Android Development",
            dateTime = "2024-10-02 14:00",
            location = "Room 203",
            description = "Hands-on workshop on modern Android development"
        ),
        Event(
            eventId = "E20241003",
            title = "Panel Discussion: Future of Mobile",
            dateTime = "2024-10-03 16:00",
            location = "Conference Room B",
            description = "Discussion about the future trends in mobile technology"
        )
    )
    

    private val passedUsers = ConcurrentHashMap<String, java.util.concurrent.ConcurrentHashMap<String, Boolean>>() // eventId to userId map
    
    override fun getUserEvents(userId: String): Single<List<Event>> {
        return Single.fromCallable {

            Thread.sleep((200..500).random().toLong())
            events
        }.delay(200L + (0..300).random(), TimeUnit.MILLISECONDS)
    }

    override fun getEventDetails(eventId: String): Single<Event> {
        return Single.fromCallable {

            Thread.sleep((200..500).random().toLong())
            events.find { it.eventId == eventId } ?: throw RuntimeException("Event not found: $eventId")
        }.delay(200L + (0..300).random(), TimeUnit.MILLISECONDS)
    }
    
    override fun validateAccessTicket(ticket: AccessTicket): Single<AccessDecision> {
        return Single.fromCallable {

            Thread.sleep((200..500).random().toLong())

            val usersForEvent = passedUsers.computeIfAbsent(ticket.eventId) {
                java.util.concurrent.ConcurrentHashMap()
            }


            val counter = usersForEvent.size
            val status = if (usersForEvent.containsKey(ticket.userId)) {
                AccessStatus.ALREADY_PASSED
            } else {
                AccessStatus.NOT_PASSED
            }

            AccessDecision(
                userId = ticket.userId,
                eventId = ticket.eventId,
                status = status,
                decision = null,
                counter = counter
            )
        }.delay(200L + (0..300).random(), TimeUnit.MILLISECONDS)
    }
    
    override fun processAccessDecision(userId: String, eventId: String, allow: Boolean): Single<AccessDecision> {
        return Single.fromCallable {

            Thread.sleep((200..500).random().toLong())

            val usersForEvent = passedUsers.computeIfAbsent(eventId) {
                java.util.concurrent.ConcurrentHashMap()
            }
            val originalCounter = usersForEvent.size

            val decision = if (allow) {
                usersForEvent[userId] = true // Add user to the map
                DecisionResult.ALLOWED
            } else {
                DecisionResult.DENIED
            }


            val counter = if (allow) usersForEvent.size else originalCounter

            val status = if (usersForEvent.containsKey(userId)) {
                AccessStatus.ALREADY_PASSED
            } else {
                AccessStatus.NOT_PASSED
            }

            AccessDecision(
                userId = userId,
                eventId = eventId,
                status = status,
                decision = decision,
                counter = counter
            )
        }.delay(200L + (0..300).random(), TimeUnit.MILLISECONDS)
    }
}