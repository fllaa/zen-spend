package com.flla.example.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow

@Composable
fun <T> StateFlow<T>.collectUiState(): State<T> = collectAsStateWithLifecycle()
