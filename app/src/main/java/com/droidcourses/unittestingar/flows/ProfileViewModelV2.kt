package com.droidcourses.unittestingar.flows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidcourses.unittestingar.coroutines.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException


class ProfileViewModel(private val profileUseCase: GetUserProfileV2) : ViewModel() {
    var _profileUiState = MutableStateFlow<ProfileUIState>(ProfileUIState.Idle)
    val profileUIState = _profileUiState.asStateFlow()



    suspend fun getUserProfile() {
//        viewModelScope.launch {
//            println("context ${this.coroutineContext}")
//            runCatching {
//                profileUseCase.getProfileDataSync()
//            }
//                .onSuccess { _profileUiState.value = ProfileUIState.Success(it) }
//                .onFailure {
//                    _profileUiState.value = ProfileUIState.Error(it.localizedMessage ?: "")
//                }
////        }
//
//        viewModelScope.launch {
//            _profileUiState.update { ProfileUIState.Loading }
//            profileUseCase.getProfileDataSync()
//                .catch {
//                    _profileUiState.update { ProfileUIState.Error(it.toString()) }
//                }
//                .collect {
//                    it.getOrNull()?.let { profile ->
//                        _profileUiState.update { ProfileUIState.Success(profile) }
//                    }
//                }
//        }
//

        profileUseCase.getProfileDataSync()
            .onStart { _profileUiState.update { ProfileUIState.Loading } }
            .onEach {
                val profile = it.getOrNull() ?: return@onEach
                when {
                    it.isSuccess -> _profileUiState.update { ProfileUIState.Success(profile) }
                    it.isFailure -> _profileUiState.update { ProfileUIState.Error(it.toString()) }
                }
            }
            .catch { e->
                _profileUiState.update { ProfileUIState.Error(e.message ?: "") }
            }
            .launchIn(viewModelScope)



    }
}

sealed class ProfileUIState {
    data object Idle : ProfileUIState()
    data object Loading : ProfileUIState()
    data class Success(val data: Profile) : ProfileUIState()
    data class Error(val message: String) : ProfileUIState()
}