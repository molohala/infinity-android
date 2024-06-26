package com.molohala.grow.ui.main.profile.setting.baekjoonsetting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.molohala.grow.common.flow.FetchFlow
import com.bestswlkh0310.mydesignsystem.R
import com.bestswlkh0310.mydesignsystem.component.button.MyCTAButton
import com.bestswlkh0310.mydesignsystem.component.dialog.MyDialog
import com.bestswlkh0310.mydesignsystem.component.textfield.MyTextField
import com.bestswlkh0310.mydesignsystem.component.topappbar.MyTopAppBar
import com.bestswlkh0310.mydesignsystem.foundation.MyTheme
import com.molohala.grow.specific.text.Headline
import com.molohala.grow.ui.root.AppViewModel

@Composable
fun BaekjoonSettingScreen(
    navController: NavController,
    appViewModel: AppViewModel,
    viewModel: BaekjoonSettingViewModel = viewModel()
) {

    val uiAppState by appViewModel.uiState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    var showPatchBaekjoonSettingSuccessDialog by remember { mutableStateOf(false) }
    var showPatchBaekjoonSettingFailureDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val profile = uiAppState.profile as? FetchFlow.Success
        val baekjoon = profile?.data?.socialAccounts?.firstOrNull { it.socialType == "SOLVED_AC" }
        viewModel.updateBaekjoonId(id = baekjoon?.socialId ?: "")
        viewModel.uiEffect.collect {
            when (it) {
                BaekjoonSettingSideEffect.PatchBaekjoonSettingFailure -> {
                    showPatchBaekjoonSettingFailureDialog = true
                }

                BaekjoonSettingSideEffect.PatchBaekjoonSettingSuccess -> {
                    showPatchBaekjoonSettingSuccessDialog = true
                }
            }
        }
    }

    MyTopAppBar(
        text = "백준 설정",
        onClickBackButton = {
            navController.popBackStack()
        },
        backgroundColor = MyTheme.colorScheme.backgroundAlt
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row {
                Headline(
                    modifier = Modifier
                        .padding(top = 20.dp, start = 4.dp),
                    text = "백준 ID"
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            MyTextField(
                value = uiState.baekjoonId,
                onValueChange = viewModel::updateBaekjoonId,
                hint = "",
            )
            Spacer(modifier = Modifier.weight(1f))
            MyCTAButton(
                modifier = Modifier
                    .padding(bottom = 8.dp),
                leftIcon = R.drawable.ic_check,
                text = "완료",
                enabled = uiState.baekjoonId.isNotEmpty()
            ) {
                viewModel.patchGithubId()
            }
        }
    }
    if (showPatchBaekjoonSettingSuccessDialog) {
        MyDialog(
            title = "백준 정보 수정 성공",
            onDismissRequest = {
                showPatchBaekjoonSettingSuccessDialog = false
                navController.popBackStack()
            },
        )
    }

    if (showPatchBaekjoonSettingFailureDialog) {
        MyDialog(
            title = "백준 정보 수정 실패",
            content = "아이디를 다시 확인해 주세요",
            onDismissRequest = {
                showPatchBaekjoonSettingFailureDialog = false
            },
        )
    }
}