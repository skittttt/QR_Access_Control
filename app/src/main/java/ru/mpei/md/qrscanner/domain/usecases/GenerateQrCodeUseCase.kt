package ru.mpei.md.qrscanner.domain.usecases

import com.google.gson.Gson
import io.reactivex.rxjava3.core.Single
import ru.mpei.md.qrscanner.data.models.AccessTicket

class GenerateQrCodeUseCase {
    private val gson = Gson()
    
    operator fun invoke(userId: String, eventId: String): Single<String> {
        return Single.fromCallable {
            val ticket = AccessTicket(userId = userId, eventId = eventId)
            gson.toJson(ticket)
        }
    }
}