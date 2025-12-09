package ru.mpei.md.qrscanner.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.mpei.md.qrscanner.data.models.AccessDecision
import ru.mpei.md.qrscanner.data.models.AccessTicket
import ru.mpei.md.qrscanner.domain.usecases.ProcessAccessDecisionUseCase
import ru.mpei.md.qrscanner.domain.usecases.ValidateAccessTicketUseCase
import ru.mpei.md.qrscanner.domain.usecases.ValidateQrCodeUseCase
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val validateQrCodeUseCase: ValidateQrCodeUseCase,
    private val validateAccessTicketUseCase: ValidateAccessTicketUseCase,
    private val processAccessDecisionUseCase: ProcessAccessDecisionUseCase
) : ViewModel() {

    private val _scanResult = MutableStateFlow<AccessDecision?>(null)
    val scanResult: StateFlow<AccessDecision?> = _scanResult.asStateFlow()

    private val _ticketData = MutableStateFlow<AccessTicket?>(null)
    val ticketData: StateFlow<AccessTicket?> = _ticketData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _accessDecisionResult = MutableStateFlow<String?>(null)
    val accessDecisionResult: StateFlow<String?> = _accessDecisionResult.asStateFlow()

    fun processQrCode(qrCodeContent: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _scanResult.value = null // Clear previous scan result
            _accessDecisionResult.value = null // Clear previous decision result

            validateQrCodeUseCase(qrCodeContent)
                .subscribe(
                    { ticket ->
                        _ticketData.value = ticket
                        validateTicket(ticket)
                    },
                    { throwable ->
                        _error.value = "Invalid QR code: ${throwable.message}"
                        _isLoading.value = false
                    }
                )
        }
    }

    private fun validateTicket(ticket: AccessTicket) {
        // Validate the ticket using the repository which will provide the counter
        validateAccessTicketUseCase(ticket)
            .subscribe(
                { decision ->
                    _scanResult.value = decision
                    _isLoading.value = false
                },
                { throwable ->
                    _error.value = "Ticket validation failed: ${throwable.message}"
                    _scanResult.value = null // Clear scan result on error
                    _isLoading.value = false
                }
            )
    }

    fun processAccessDecision(allow: Boolean) {
        val ticket = _ticketData.value
        if (ticket != null) {
            viewModelScope.launch {
                _isLoading.value = true
                _error.value = null
                _scanResult.value = null // Clear previous scan result before processing new decision

                processAccessDecisionUseCase(ticket.userId, ticket.eventId, allow)
                    .subscribe(
                        { decision ->
                            _scanResult.value = decision
                            _accessDecisionResult.value = if (allow) "Allowed" else "Denied"
                            _isLoading.value = false

                            // Reset ticket data so the next scan will work properly
                            _ticketData.value = null
                        },
                        { throwable ->
                            _error.value = throwable.message
                            _isLoading.value = false
                        }
                    )
            }
        }
    }
}