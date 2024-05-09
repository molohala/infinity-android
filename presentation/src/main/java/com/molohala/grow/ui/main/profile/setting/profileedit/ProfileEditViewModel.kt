package com.molohala.grow.ui.main.profile.setting.profileedit

import androidx.lifecycle.ViewModel
import com.molohala.grow.common.flow.FetchFlow
import com.molohala.grow.data.global.RetrofitClient
import com.molohala.grow.data.info.request.PatchProfileRequest
import com.molohala.grow.data.language.request.PatchMyLanguageRequest
import com.molohala.grow.data.language.response.LanguageResponse
import com.molohala.grow.ui.util.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProfileEditState(
    val languages: FetchFlow<List<LanguageResponse>> = FetchFlow.Fetching(),
    val myLanguages: FetchFlow<List<LanguageResponse>> = FetchFlow.Fetching(),
    val bio: String = "",
    val job: String = "",
)

sealed interface ProfileEditSideEffect {
    data object FetchFailure: ProfileEditSideEffect
    data object PatchSuccess: ProfileEditSideEffect
    data object PatchFailure: ProfileEditSideEffect
}

class ProfileEditViewModel: ViewModel() {

    private val _uiSideEffect = MutableSharedFlow<ProfileEditSideEffect>()
    val uiSideEffect = _uiSideEffect.asSharedFlow()

    private val _uiState = MutableStateFlow(ProfileEditState())
    val uiState = _uiState.asStateFlow()

    fun fetchLanguages() {
        launch {
            try {
                _uiState.update { it.copy(languages = FetchFlow.Fetching()) }
                val languages = RetrofitClient.languageApi.getLanguage().data?: return@launch
                _uiState.update { it.copy(languages = FetchFlow.Success(languages)) }
            } catch (e: Exception) {
                _uiState.update { it.copy(languages = FetchFlow.Failure()) }
                _uiSideEffect.emit(ProfileEditSideEffect.FetchFailure)
            }
        }
    }

    fun fetchMyLanguages() {
        launch {
            try {
                _uiState.update { it.copy(myLanguages = FetchFlow.Fetching()) }
                val languages = RetrofitClient.languageApi.getMyLanguage().data?: return@launch
                _uiState.update { it.copy(myLanguages = FetchFlow.Success(languages)) }
            } catch (e: Exception) {
                _uiState.update { it.copy(myLanguages = FetchFlow.Failure()) }
                _uiSideEffect.emit(ProfileEditSideEffect.FetchFailure)
            }
        }
    }

    fun updateMyLanguages(lang: LanguageResponse) {
        val myLangs = (_uiState.value.myLanguages as? FetchFlow.Success)?.data?: return
        val newMyLanges = myLangs.toMutableList()
        if (myLangs.contains(lang)) {
            newMyLanges.remove(lang)
        } else {
            newMyLanges.add(lang)
        }
        _uiState.update { it.copy(myLanguages = FetchFlow.Success(newMyLanges)) }
    }

    fun completeSetting() {
        val langs = (_uiState.value.myLanguages as? FetchFlow.Success)?.data?: return
        val patchMyLanguageRequest = PatchMyLanguageRequest(
            langs = langs.map { it.id }
        )
        val patchProfileRequest = PatchProfileRequest(
            bio = _uiState.value.bio,
            job = _uiState.value.job
        )
        launch {
            try {
                RetrofitClient.languageApi.patchLanguage(patchMyLanguageRequest)
                RetrofitClient.infoApi.patchProfile(patchProfileRequest)
                _uiSideEffect.emit(ProfileEditSideEffect.PatchSuccess)
            } catch (e: Exception) {
                _uiSideEffect.emit(ProfileEditSideEffect.PatchFailure)
            }
        }
    }

    fun updateBio(bio: String) {
        _uiState.update { it.copy(bio = bio) }
    }

    fun updateJob(job: String) {
        _uiState.update { it.copy(job = job) }
    }
}