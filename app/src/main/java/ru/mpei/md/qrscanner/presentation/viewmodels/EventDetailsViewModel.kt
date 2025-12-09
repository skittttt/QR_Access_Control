package ru.mpei.md.qrscanner.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.mpei.md.qrscanner.domain.models.DomainEvent
import ru.mpei.md.qrscanner.domain.usecases.GenerateQrCodeUseCase
import ru.mpei.md.qrscanner.domain.usecases.GetEventDetailsUseCase
import javax.inject.Inject

@HiltViewModel
class EventDetailsViewModel @Inject constructor(
    private val getEventDetailsUseCase: GetEventDetailsUseCase,
    private val generateQrCodeUseCase: GenerateQrCodeUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _event = MutableStateFlow<DomainEvent?>(null)
    val event: StateFlow<DomainEvent?> = _event.asStateFlow()
    
    private val _qrCodeContent = MutableStateFlow<String?>(null)
    val qrCodeContent: StateFlow<String?> = _qrCodeContent.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadEventDetails(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            getEventDetailsUseCase(eventId)
                .subscribe(
                    { event ->
                        _event.value = event
                        _isLoading.value = false
                    },
                    { throwable ->
                        _error.value = throwable.message
                        _isLoading.value = false
                    }
                )
        }
    }
    
    fun generateQrCode(userId: String, eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            generateQrCodeUseCase(userId, eventId)
                .subscribe(
                    { qrContent ->
                        _qrCodeContent.value = qrContent
                        _isLoading.value = false
                    },
                    { throwable ->
                        _error.value = throwable.message
                        _isLoading.value = false
                    }
                )
        }
    }
}