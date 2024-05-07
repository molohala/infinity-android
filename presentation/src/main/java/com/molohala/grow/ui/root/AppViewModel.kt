package com.molohala.grow.ui.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.molohala.grow.application.GrowApp
import com.molohala.grow.common.chart.baekjoonWeekChartInfo
import com.molohala.grow.common.chart.githubWeekChartInfo
import com.molohala.grow.common.flow.FetchFlow
import com.molohala.grow.data.global.RetrofitClient
import com.molohala.grow.data.info.response.GithubResponse
import com.molohala.grow.data.info.response.ProfileResponse
import com.molohala.grow.data.info.response.SolvedacResponse
import com.molohala.grow.designsystem.component.bottomtabbar.BottomTabItemType
import com.molohala.grow.designsystem.specific.chart.GrowChartInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppState(
    val accessToken: String = GrowApp.prefs.accessToken,
    val refreshToken: String = GrowApp.prefs.refreshToken,
    val profile: FetchFlow<ProfileResponse> = FetchFlow.Fetching(),
    val github: FetchFlow<GithubResponse?> = FetchFlow.Fetching(),
    val baekjoon: FetchFlow<SolvedacResponse?> = FetchFlow.Fetching(),
    val selectedTab: BottomTabItemType = BottomTabItemType.Home,
    val githubChartInfo: FetchFlow<GrowChartInfo> = FetchFlow.Fetching(),
    val baekjoonChartInfo: FetchFlow<GrowChartInfo> = FetchFlow.Fetching(),
    val isDarkMode: Boolean = GrowApp.prefs.isDarkMode,
)

class AppViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AppState())
    val uiState = _uiState.asStateFlow()

    fun updateAccessToken(token: String) {
        GrowApp.prefs.accessToken = token
        _uiState.update { it.copy(accessToken = token) }
    }

    fun updateRefreshToken(token: String) {
        GrowApp.prefs.refreshToken = token
        _uiState.update { it.copy(refreshToken = token) }
    }

    fun clearToken() {
        GrowApp.prefs.clearToken()
        with(GrowApp.prefs) {
            updateAccessToken(accessToken)
            updateRefreshToken(refreshToken)
        }
    }

    fun fetchProfile() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(profile = FetchFlow.Fetching()) }
                val profile = RetrofitClient.infoApi.getProfile().data ?: return@launch
                _uiState.update { it.copy(profile = FetchFlow.Success(profile)) }
                fetchGithub()
                fetchBaekjoon()
            } catch (e: Exception) {
                _uiState.update { it.copy(profile = FetchFlow.Failure()) }
            }
        }
    }

    fun fetchGithub() {
        val profile = uiState.value.profile as? FetchFlow.Success ?: run {
            _uiState.update { it.copy(github = FetchFlow.Failure()) }
            return
        }
        val github = profile.data.socialAccounts.firstOrNull { it.socialType == "GITHUB" } ?: run {
            _uiState.update { it.copy(github = FetchFlow.Success(null)) }
            return
        }
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(github = FetchFlow.Fetching()) }
                val githubResponse =
                    RetrofitClient.infoApi.getGithubInfo(name = github.socialId).data
                        ?: return@launch
                _uiState.update {
                    it.copy(
                        github = FetchFlow.Success(githubResponse),
                        githubChartInfo = FetchFlow.Success(githubResponse.weekCommits.githubWeekChartInfo)
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(github = FetchFlow.Failure()) }
            }
        }
    }

    fun fetchBaekjoon() {
        val profile = uiState.value.profile as? FetchFlow.Success ?: run {
            _uiState.update { it.copy(baekjoon = FetchFlow.Failure()) }
            return
        }
        val solvedac =
            profile.data.socialAccounts.firstOrNull { it.socialType == "SOLVED_AC" } ?: run {
                _uiState.update { it.copy(baekjoon = FetchFlow.Success(null)) }
                return
            }
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(baekjoon = FetchFlow.Fetching()) }
                val solvedacResponse =
                    RetrofitClient.infoApi.getSolvedacInfo(name = solvedac.socialId).data
                        ?: return@launch
                _uiState.update {
                    it.copy(
                        baekjoon = FetchFlow.Success(solvedacResponse),
                        baekjoonChartInfo = FetchFlow.Success(solvedacResponse.weekSolves.baekjoonWeekChartInfo)
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(baekjoon = FetchFlow.Failure()) }
            }
        }
    }

    fun updateIsDarkMode(isDarkMode: Boolean) {
        GrowApp.prefs.isDarkMode = isDarkMode
        _uiState.update { it.copy(isDarkMode = isDarkMode) }
    }

    fun clickTab(tab: BottomTabItemType) {
        _uiState.update { it.copy(selectedTab = tab) }
    }
}