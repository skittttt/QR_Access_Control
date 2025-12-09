package ru.mpei.md.qrscanner.domain.usecases

import io.reactivex.rxjava3.core.Single
import ru.mpei.md.qrscanner.data.repository.AccessRepository
import ru.mpei.md.qrscanner.domain.models.DomainEvent

class GetEventDetailsUseCase(private val repository: AccessRepository) {
    operator fun invoke(eventId: String): Single<DomainEvent> {
        return repository.getEventDetails(eventId)
            .map { event ->
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