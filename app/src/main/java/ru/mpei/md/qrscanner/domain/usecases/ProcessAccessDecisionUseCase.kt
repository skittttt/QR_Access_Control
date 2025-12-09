package ru.mpei.md.qrscanner.domain.usecases

import io.reactivex.rxjava3.core.Single
import ru.mpei.md.qrscanner.data.models.AccessDecision
import ru.mpei.md.qrscanner.data.repository.AccessRepository

class ProcessAccessDecisionUseCase(private val repository: AccessRepository) {
    operator fun invoke(userId: String, eventId: String, allow: Boolean): Single<AccessDecision> {
        return repository.processAccessDecision(userId, eventId, allow)
    }
}