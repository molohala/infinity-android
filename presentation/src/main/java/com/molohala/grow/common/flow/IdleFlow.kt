package com.molohala.grow.common.flow

sealed interface IdleFlow {
    data object Idle: IdleFlow
    data object Fetching: IdleFlow
    data object Success: IdleFlow
    data object Failure: IdleFlow
}