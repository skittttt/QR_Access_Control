package ru.mpei.md.qrscanner.domain.usecases

import com.google.gson.Gson
import io.reactivex.rxjava3.core.Single
import ru.mpei.md.qrscanner.data.models.AccessTicket
import ru.mpei.md.qrscanner.data.repository.AccessRepository

class ValidateQrCodeUseCase(private val repository: AccessRepository) {
    private val gson = Gson()
    
    operator fun invoke(qrCodeContent: String): Single<AccessTicket> {
        return Single.fromCallable {
            val ticket = gson.fromJson(qrCodeContent, AccessTicket::class.java)
            if (ticket.userId.isEmpty() || ticket.eventId.isEmpty()) {
                throw IllegalArgumentException("Invalid QR code content")
            }
            ticket
        }
    }
}