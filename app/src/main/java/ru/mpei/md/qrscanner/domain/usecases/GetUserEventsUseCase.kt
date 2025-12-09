package ru.mpei.md.qrscanner.domain.usecases

import io.reactivex.rxjava3.core.Single
import ru.mpei.md.qrscanner.data.repository.AccessRepository
import ru.mpei.md.qrscanner.domain.models.DomainEvent

class GetUserEventsUseCase(private val repository: AccessRepository) {
    operator fun invoke(userId: String): Single<List<DomainEvent>> {
        return repository.getUserEvents(userId)
            .map { events ->
                events.map { event ->
                    DomainEvent(
                        eventId = event.eventId,
                        title = event.title,
                        dateTime = event.dateTime,
                        location = event.location,
                        description = event.description
                    )
                }
            }
    }
}