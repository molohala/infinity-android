package com.molohala.grow.ui.main.communitydetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.molohala.grow.common.flow.FetchFlow
import com.molohala.grow.data.comment.response.CommentResponse
import com.molohala.grow.designsystem.comment.InfinityCommentCell
import com.molohala.grow.designsystem.comment.InfinityCommentCellShimmer
import com.molohala.grow.ui.root.AppViewModel

@Composable
fun CommunityDetailScreen(
    appViewModel: AppViewModel,
    viewModel: CommunityDetailViewModel = viewModel()
) {

    val uiAppState by appViewModel.uiState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Box {
        LazyColumn {
            item {
                uiState.community.let {
                    when (it) {
                        is FetchFlow.Failure -> Text(text = "불러오기 실패")
                        is FetchFlow.Fetching -> {

                        }
                        is FetchFlow.Success -> TODO()
                    }
                }
            }
        }
    }
}

@Composable
private fun Info(
    onClickPatchLike: () -> Unit
) {
    Row {
        Button(onClick = onClickPatchLike) {
            Text(text = "여기에 하트")
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun Comments(
    uiState: CommunityDetailState,
    onClickRemoveComment: (CommentResponse) -> Unit
) {
    uiState.comments.let {
        when (it) {
            is FetchFlow.Failure -> Text(text = "불러오기 실패")
            is FetchFlow.Fetching -> {
                InfinityCommentCellShimmer()
            }
            is FetchFlow.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(it.data, key = { comment -> comment.commentId }) { comment ->
                        InfinityCommentCell(comment = comment) {
                            onClickRemoveComment(comment)
                        }
                    }

                }
            }
        }
    }
}