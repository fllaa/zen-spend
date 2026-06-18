package com.flla.zenspend.feature.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flla.zenspend.core.designsystem.component.ZenSpendTextField
import com.flla.zenspend.core.designsystem.theme.LocalZenSpendSpacing
import com.flla.zenspend.core.ui.collectUiState

@Composable
fun LoginRoute(
    onLoggedIn: () -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectUiState()
    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) onLoggedIn()
    }
    LoginScreen(
        state = state,
        actions =
            LoginActions(
                onEmailChanged = viewModel::onEmailChanged,
                onPasswordChanged = viewModel::onPasswordChanged,
                onSubmit = viewModel::submit,
                onRegisterClick = onRegisterClick,
            ),
    )
}

data class LoginActions(
    val onEmailChanged: (String) -> Unit,
    val onPasswordChanged: (String) -> Unit,
    val onSubmit: () -> Unit,
    val onRegisterClick: () -> Unit,
)

@Composable
fun LoginScreen(
    state: LoginUiState,
    actions: LoginActions,
) {
    val spacing = LocalZenSpendSpacing.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .drawBehind {
                    // Soft background glowing elements
                    drawRect(
                        brush =
                            Brush.radialGradient(
                                colors = listOf(primaryColor.copy(alpha = 0.08f), Color.Transparent),
                                center = Offset(size.width * 0.8f, size.height * 0.15f),
                                radius = size.minDimension * 0.8f,
                            ),
                    )
                    drawRect(
                        brush =
                            Brush.radialGradient(
                                colors = listOf(secondaryColor.copy(alpha = 0.05f), Color.Transparent),
                                center = Offset(size.width * 0.2f, size.height * 0.85f),
                                radius = size.minDimension * 0.8f,
                            ),
                    )
                },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(modifier = Modifier.height(spacing.xl))

            // Brand Identity
            Box(
                modifier =
                    Modifier
                        .size(96.dp)
                        .drawBehind {
                            // Ambient shadow for the icon circle
                            drawCircle(
                                color = primaryColor.copy(alpha = 0.05f),
                                radius = size.minDimension / 2f + 4.dp.toPx(),
                            )
                        }
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Eco,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(48.dp),
                )
            }
            Spacer(modifier = Modifier.height(spacing.md))
            Text(
                text = "ZenSpend",
                style =
                    MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp,
                    ),
                color = primaryColor,
            )
            Text(
                text = "Kelola keuangan dengan tenang.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = spacing.xs),
            )

            Spacer(modifier = Modifier.height(spacing.xl))

            // Sign In Glass Card
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(16.dp),
                            ambientColor = Color(0xFF2D7D9A).copy(alpha = 0.1f),
                            spotColor = Color(0xFF2D7D9A).copy(alpha = 0.1f),
                        ),
                shape = RoundedCornerShape(16.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                    ),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(spacing.md),
                ) {
                    Text(
                        text = "Masuk ke Akun",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    // Email Field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Email",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 4.dp),
                        )
                        ZenSpendTextField(
                            value = state.email,
                            onValueChange = actions.onEmailChanged,
                            label = "nama@email.com",
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .testTag("login_email"),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Email,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        )
                    }

                    // Password Field
                    var passwordVisible by remember { mutableStateOf(false) }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(start = 4.dp, end = 4.dp, bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Kata Sandi",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = "Lupa sandi?",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                                color = primaryColor,
                                modifier = Modifier.clickable { /* No-op */ },
                            )
                        }
                        ZenSpendTextField(
                            value = state.password,
                            onValueChange = actions.onPasswordChanged,
                            label = "••••••••",
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .testTag("login_password"),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Lock,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                )
                            },
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff
                                val description = if (passwordVisible) "Sembunyikan sandi" else "Tampilkan sandi"
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = image,
                                        contentDescription = description,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        )
                    }

                    // Error Message
                    if (state.errorMessage != null) {
                        Text(
                            text = state.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 4.dp),
                        )
                    }

                    // Sign In Submit Button
                    Button(
                        onClick = actions.onSubmit,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("login_submit"),
                        enabled = !state.isLoading,
                        shape = CircleShape,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = primaryColor,
                            ),
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text(
                                text = "Masuk",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            )
                        }
                    }

                    // Divider
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant,
                        )
                        Text(
                            text = "ATAU",
                            style =
                                MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 2.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(horizontal = spacing.md),
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant,
                        )
                    }

                    // Social Auth Button
                    OutlinedButton(
                        onClick = { /* No-op */ },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        colors =
                            ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            GoogleLogoIcon(modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Lanjutkan dengan Google",
                                style =
                                    MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    ),
                            )
                        }
                    }

                    // Register Navigation Link
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = spacing.xs),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Belum punya akun? ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "Buat akun",
                            style =
                                MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = primaryColor,
                                ),
                            modifier = Modifier.clickable { actions.onRegisterClick() },
                        )
                    }
                }
            }

            // Disclaimer Footer
            Text(
                text = "Dengan masuk, Anda menyetujui Ketentuan Layanan dan Kebijakan Privasi kami yang mengutamakan keamanan data finansial Anda.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = spacing.md, end = spacing.md, top = spacing.xl),
            )

            Spacer(modifier = Modifier.height(spacing.xl))
        }
    }
}

@Composable
fun GoogleLogoIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val scaleX = size.width / 24f
        val scaleY = size.height / 24f
        scale(scaleX, scaleY, pivot = Offset.Zero) {
            // Red path
            val redPath =
                Path().apply {
                    moveTo(12f, 5.38f)
                    cubicTo(13.62f, 5.38f, 15.06f, 5.94f, 16.21f, 7.02f)
                    lineTo(19.36f, 3.87f)
                    cubicTo(17.45f, 2.09f, 14.97f, 1f, 12f, 1f)
                    cubicTo(7.7f, 1f, 3.99f, 3.47f, 2.18f, 7.06f)
                    lineTo(5.84f, 9.91f)
                    cubicTo(6.71f, 7.31f, 9.14f, 5.38f, 12f, 5.38f)
                    close()
                }
            drawPath(redPath, Color(0xFFEA4335))

            // Green path
            val greenPath =
                Path().apply {
                    moveTo(12f, 23f)
                    cubicTo(14.97f, 23f, 17.46f, 22.02f, 19.28f, 20.34f)
                    lineTo(15.71f, 17.57f)
                    cubicTo(14.73f, 18.23f, 13.48f, 18.63f, 12f, 18.63f)
                    cubicTo(9.14f, 18.63f, 6.71f, 16.7f, 5.84f, 14.1f)
                    lineTo(2.18f, 14.1f)
                    lineTo(2.18f, 16.94f)
                    cubicTo(3.99f, 20.53f, 7.7f, 23f, 12f, 23f)
                    close()
                }
            drawPath(greenPath, Color(0xFF34A853))

            // Yellow path
            val yellowPath =
                Path().apply {
                    moveTo(5.84f, 14.09f)
                    cubicTo(5.62f, 13.43f, 5.49f, 12.73f, 5.49f, 12f)
                    cubicTo(5.49f, 11.27f, 5.62f, 10.57f, 5.84f, 9.91f)
                    lineTo(5.84f, 7.06f)
                    lineTo(2.18f, 7.06f)
                    cubicTo(1.43f, 8.55f, 1f, 10.22f, 1f, 12f)
                    cubicTo(1f, 13.78f, 1.43f, 15.45f, 2.18f, 16.94f)
                    lineTo(5.84f, 14.09f)
                    close()
                }
            drawPath(yellowPath, Color(0xFFFBBC05))

            // Blue path
            val bluePath =
                Path().apply {
                    moveTo(22.56f, 12.25f)
                    cubicTo(22.56f, 11.47f, 22.49f, 10.72f, 22.36f, 10f)
                    lineTo(12f, 10f)
                    lineTo(12f, 14.26f)
                    lineTo(17.92f, 14.26f)
                    cubicTo(17.66f, 15.63f, 16.88f, 16.7f, 15.71f, 17.57f)
                    lineTo(15.71f, 20.34f)
                    lineTo(19.28f, 20.34f)
                    cubicTo(21.36f, 18.42f, 22.56f, 15.6f, 22.56f, 12.25f)
                    close()
                }
            drawPath(bluePath, Color(0xFF4285F4))
        }
    }
}
