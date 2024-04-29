package com.molohala.infinity.ui.main.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.molohala.infinity.designsystem.github.InfinityChartCell
import com.molohala.infinity.designsystem.color.InfinityColor
import com.molohala.infinity.data.info.response.ProfileResponse
import com.molohala.infinity.designsystem.typo.TopBar
import com.molohala.infinity.extension.applyCardView
import com.molohala.infinity.extension.bounceClick
import com.molohala.infinity.icon.IconLogout
import com.molohala.infinity.ui.main.main.NavGroup
import com.molohala.infinity.ui.main.statcell.InfinityStatCell
import com.molohala.infinity.ui.main.statcell.InfinityStatType
import com.molohala.infinity.ui.root.AppViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    appViewModel: AppViewModel,
    profileViewModel: ProfileViewModel = viewModel()
) {

    val scrollState = rememberScrollState()
    val uiAppState by appViewModel.uiState.collectAsState()
    val uiState by profileViewModel.uiState.collectAsState()
    val coroutine = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefresh,
        onRefresh = {
            coroutine.launch {
                appViewModel.fetchProfile()
                profileViewModel.refresh()
            }
        }
    )
    TopBar(
        modifier = Modifier,
        backgroundColor = InfinityColor.background,
        text = "프로필"
    ) {
        Column(
            modifier = Modifier
                .background(InfinityColor.background)
                .padding(horizontal = 16.dp)
                .fillMaxHeight()
                .verticalScroll(state = scrollState)
                .pullRefresh(pullRefreshState)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            uiAppState.profile?.let {
                Profile(profile = it) {
                    navController.navigate(NavGroup.Setting.name)
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                uiAppState.github?.let {
                    InfinityStatCell(
                        modifier = Modifier
                            .weight(1f),
                        title = "커밋 개수",
                        type = InfinityStatType.Github(commit = it.totalCommits)
                    ) {

                    }
                }
                uiAppState.solvedac?.let {
                    InfinityStatCell(
                        modifier = Modifier
                            .weight(1f),
                        title = "문제 푼 개수",
                        type = InfinityStatType.Baekjoon(solved = it.totalSolves)
                    ) {

                    }
                }
            }
            InfinityChartCell {

            }
        }
    }
}

@Composable
fun Profile(
    profile: ProfileResponse,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .applyCardView(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Text(
                modifier = Modifier
                    .padding(start = 8.dp),
                text = profile.name,
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(InfinityColor.background)
                    .padding(vertical = 8.dp, horizontal = 12.dp)
                    .bounceClick(onClick = onClick),
                text = "설정",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }

        Text(
            modifier = Modifier
                .padding(vertical = 24.dp),
            text = "\"뚝딱뚝딱\"",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
    }
}

@Composable
fun Logout(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .bounceClick(onClick = onClick)
            .applyCardView(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(4.dp),
            text = "로그아웃",
            color = Color.Red,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.weight(1f))
        IconLogout()
    }
}