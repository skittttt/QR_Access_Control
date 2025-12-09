package ru.mpei.md.qrscanner.domain.usecases

import io.reactivex.rxjava3.core.Single
import ru.mpei.md.qrscanner.data.models.AccessDecision
import ru.mpei.md.qrscanner.data.models.AccessTicket
import ru.mpei.md.qrscanner.data.repository.AccessRepository

class ValidateAccessTicketUseCase(private val repository: AccessRepository) {
    operator fun invoke(ticket: AccessTicket): Single<AccessDecision> {
        return repository.validateAccessTicket(ticket)
    }
}