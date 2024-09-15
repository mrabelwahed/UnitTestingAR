package com.droidcourses.unittestingar.coroutines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val profileUseCase: GetUserProfile) : ViewModel() {
    var _profileUiState = MutableStateFlow<ProfileUIState>(ProfileUIState.Idle)
    val profileUIState = _profileUiState.asStateFlow()

    init {
        getUserProfile()
    }

    fun getUserProfile() {
        viewModelScope.launch {
            println("context ${this.coroutineContext}")
            runCatching {
                profileUseCase.getProfileDataAsync()
            }
                .onSuccess { _profileUiState.value = ProfileUIState.Success(it) }
                .onFailure {
                    _profileUiState.value = ProfileUIState.Error(
                        it.localizedMessage ?: ""
                    )
                }
        }
    }
}

sealed class ProfileUIState {
    data object Idle : ProfileUIState()
    data object Loading : ProfileUIState()
    data class Success(val data: Profile) : ProfileUIState()
    data class Error(val message: String) : ProfileUIState()
}