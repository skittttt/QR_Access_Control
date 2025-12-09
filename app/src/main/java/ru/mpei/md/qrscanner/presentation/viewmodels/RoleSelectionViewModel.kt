package ru.mpei.md.qrscanner.presentation.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RoleSelectionViewModel @Inject constructor() : ViewModel() {
    
    fun selectUserRole() {
        // User selected - navigate to user events screen
    }
    
    fun selectAdminRole() {
        // Admin selected - navigate to scanner screen
    }
}