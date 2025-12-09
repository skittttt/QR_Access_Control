package ru.mpei.md.qrscanner.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.mpei.md.qrscanner.domain.models.DomainEvent
import ru.mpei.md.qrscanner.domain.usecases.GetUserEventsUseCase
import javax.inject.Inject

@HiltViewModel
class UserEventsViewModel @Inject constructor(
    private val getUserEventsUseCase: GetUserEventsUseCase
) : ViewModel() {
    
    private val _events = MutableStateFlow<List<DomainEvent>>(emptyList())
    val events: StateFlow<List<DomainEvent>> = _events.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadUserEvents(userId: String = "U12345") { // Default user ID for demo
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            getUserEventsUseCase(userId)
                .subscribe(
                    { eventsList ->
                        _events.value = eventsList
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