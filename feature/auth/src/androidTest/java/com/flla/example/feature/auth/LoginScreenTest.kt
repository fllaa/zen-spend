package com.flla.example.feature.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.flla.example.core.designsystem.theme.ExampleTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loginScreen_displaysForm_andSubmits() {
        var submitted = false

        composeRule.setContent {
            ExampleTheme {
                LoginScreen(
                    state = LoginUiState(),
                    actions =
                        LoginActions(
                            onEmailChanged = {},
                            onPasswordChanged = {},
                            onSubmit = { submitted = true },
                            onRegisterClick = {},
                        ),
                )
            }
        }

        composeRule.onNodeWithText("Welcome back").assertIsDisplayed()
        composeRule.onNodeWithTag("login_email").assertIsDisplayed()
        composeRule.onNodeWithTag("login_password").assertIsDisplayed()
        composeRule.onNodeWithTag("login_submit").performClick()

        assertTrue(submitted)
    }
}
