package com.flla.zenspend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flla.zenspend.core.designsystem.theme.ZenSpendTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state = viewModel.uiState.collectAsStateWithLifecycle().value
            ZenSpendTheme(themeMode = state.themeMode) {
                ZenSpendApp(
                    sessionState = state.sessionState,
                    hasCompletedOnboarding = state.hasCompletedOnboarding,
                    hasCompletedSetup = state.hasCompletedSetup,
                )
            }
        }
    }
}
