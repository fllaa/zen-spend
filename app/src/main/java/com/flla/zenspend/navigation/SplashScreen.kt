package com.flla.zenspend.navigation

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun SplashScreen(sessionExpired: Boolean) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val background = MaterialTheme.colorScheme.background

    // Entrance Animation State
    var animationStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animationStarted = true
    }

    // Logo animations: scale-up and slide up
    val logoScale by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0.85f,
        animationSpec = tween(
            durationMillis = 1200,
            easing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)
        ),
        label = "logo_scale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = EaseInOutCubic
        ),
        label = "logo_alpha"
    )
    val logoTranslationY by animateFloatAsState(
        targetValue = if (animationStarted) 0f else 40f,
        animationSpec = tween(
            durationMillis = 1200,
            easing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)
        ),
        label = "logo_translation"
    )

    // Text animations with delayed fade-in
    val titleAlpha by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "title_alpha"
    )
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (animationStarted) 0.7f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 750,
            easing = FastOutSlowInEasing
        ),
        label = "subtitle_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        // 1. Ambient Background Decoration (Floating Orbs)
        AmbientOrb(
            color = primaryColor.copy(alpha = 0.08f),
            initialOffsetX = (-120).dp,
            initialOffsetY = (-80).dp,
            targetOffsetX = (-70).dp,
            targetOffsetY = (-30).dp,
            durationMillis = 12000,
            size = 300.dp
        )
        AmbientOrb(
            color = secondaryColor.copy(alpha = 0.08f),
            initialOffsetX = 80.dp,
            initialOffsetY = 120.dp,
            targetOffsetX = 40.dp,
            targetOffsetY = 70.dp,
            durationMillis = 15000,
            size = 260.dp
        )

        // 2. Custom Zen Ensō Circle (Drawing at the bottom background layer)
        EnsoCircle(
            primaryColor = primaryColor,
            secondaryColor = secondaryColor,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.25f)
        )

        // 3. Central Content Canvas
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer {
                translationY = logoTranslationY
                alpha = logoAlpha
                scaleX = logoScale
                scaleY = logoScale
            }
        ) {
            // Centered Logo with soft teal shadow/glow
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(primaryColor.copy(alpha = 0.15f), Color.Transparent),
                                radius = 64.dp.toPx()
                            ),
                            radius = 64.dp.toPx()
                        )
                    }
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Eco,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Brand Title
            Text(
                text = "ZenSpend",
                color = primaryColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.5).sp,
                modifier = Modifier.alpha(titleAlpha)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Brand Subtitle
            Text(
                text = "Track calmly. Spend wisely.",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.5.sp,
                modifier = Modifier.alpha(subtitleAlpha)
            )
        }

        // 4. Loading Indicator or Warning at the bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .offset(y = (-80).dp),
            contentAlignment = Alignment.Center
        ) {
            if (sessionExpired) {
                Text(
                    text = "Session expired",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.alpha(subtitleAlpha)
                )
            } else {
                LoaderBar(
                    primaryColor = primaryColor,
                    modifier = Modifier.alpha(subtitleAlpha)
                )
            }
        }
    }
}

@Composable
private fun AmbientOrb(
    color: Color,
    initialOffsetX: Dp,
    initialOffsetY: Dp,
    targetOffsetX: Dp,
    targetOffsetY: Dp,
    durationMillis: Int,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val initialOffsetXPx = with(density) { initialOffsetX.toPx() }
    val initialOffsetYPx = with(density) { initialOffsetY.toPx() }
    val targetOffsetXPx = with(density) { targetOffsetX.toPx() }
    val targetOffsetYPx = with(density) { targetOffsetY.toPx() }

    val transition = rememberInfiniteTransition(label = "ambient_orb_drift")
    val offsetX by transition.animateFloat(
        initialValue = initialOffsetXPx,
        targetValue = targetOffsetXPx,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetX"
    )
    val offsetY by transition.animateFloat(
        initialValue = initialOffsetYPx,
        targetValue = targetOffsetYPx,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(size)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(color, Color.Transparent),
                    center = Offset(with(density) { size.toPx() / 2 }, with(density) { size.toPx() / 2 }),
                    radius = with(density) { size.toPx() / 2 }
                )
            )
    )
}

@Composable
private fun EnsoCircle(
    primaryColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val radius = min(width, height) * 0.45f
        val center = Offset(width / 2, height * 0.65f) // Positioned lower for Zen negative-space aesthetic

        // 1. Soft underlying glow
        drawArc(
            brush = Brush.radialGradient(
                colors = listOf(secondaryColor.copy(alpha = 0.1f), Color.Transparent),
                center = center,
                radius = radius * 1.5f
            ),
            startAngle = 30f,
            sweepAngle = 310f,
            useCenter = false,
            style = Stroke(width = 60.dp.toPx(), cap = StrokeCap.Round)
        )

        // 2. Main body sweep gradient stroke
        val mainBrush = Brush.sweepGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.0f),
                primaryColor.copy(alpha = 0.15f),
                secondaryColor.copy(alpha = 0.5f),
                primaryColor.copy(alpha = 0.7f),
                secondaryColor.copy(alpha = 0.3f),
                primaryColor.copy(alpha = 0.0f)
            ),
            center = center
        )

        drawArc(
            brush = mainBrush,
            startAngle = 45f,
            sweepAngle = 290f,
            useCenter = false,
            style = Stroke(
                width = 24.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // 3. Dynamic layered watercolor texturing
        drawArc(
            color = primaryColor.copy(alpha = 0.25f),
            startAngle = 55f,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(
                width = 6.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        drawArc(
            color = secondaryColor.copy(alpha = 0.2f),
            startAngle = 70f,
            sweepAngle = 240f,
            useCenter = false,
            style = Stroke(
                width = 10.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}

@Composable
private fun LoaderBar(
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "loader_progress")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Box(
        modifier = modifier
            .width(64.dp)
            .height(4.dp)
            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f), RoundedCornerShape(2.dp))
            .clip(RoundedCornerShape(2.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val barWidth = size.width
            val barHeight = size.height
            val indicatorWidth = barWidth * 0.35f
            val startX = -indicatorWidth + (barWidth + indicatorWidth) * progress

            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(startX, 0f),
                size = Size(indicatorWidth, barHeight),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
        }
    }
}

