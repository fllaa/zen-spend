package com.flla.zenspend

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.flla.zenspend.core.model.SessionState
import com.flla.zenspend.feature.auth.AuthRoutes
import com.flla.zenspend.feature.auth.authGraph
import com.flla.zenspend.feature.home.HomeRoutes
import com.flla.zenspend.feature.home.homeGraph
import com.flla.zenspend.feature.onboarding.OnboardingRoutes
import com.flla.zenspend.feature.onboarding.onboardingGraph
import com.flla.zenspend.feature.profile.ProfileRoutes
import com.flla.zenspend.feature.profile.profileScreen
import com.flla.zenspend.feature.settings.settingsScreen
import com.flla.zenspend.feature.setup.SetupRoutes
import com.flla.zenspend.feature.setup.setupGraph
import com.flla.zenspend.navigation.SplashScreen
import com.flla.zenspend.navigation.ZenSpendRoutes

private val mainDestinations =
    listOf(
        MainDestination(
            route = HomeRoutes.GRAPH,
            selectedRoute = HomeRoutes.HOME,
            label = "Home",
            icon = Icons.Rounded.Home,
        ),
        MainDestination(
            route = ProfileRoutes.PROFILE,
            selectedRoute = ProfileRoutes.PROFILE,
            label = "Profile",
            icon = Icons.Rounded.Person,
        ),
    )

@Composable
fun ZenSpendApp(
    sessionState: SessionState,
    hasCompletedOnboarding: Boolean,
    hasCompletedSetup: Boolean,
) {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = mainDestinations.any { it.selectedRoute == currentRoute }

    SessionNavigationEffect(
        navController = navController,
        sessionState = sessionState,
        hasCompletedOnboarding = hasCompletedOnboarding,
        hasCompletedSetup = hasCompletedSetup,
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                MainBottomBar(
                    destinations = mainDestinations,
                    currentRoute = currentRoute,
                    onDestinationClick = navController::navigateToMainDestination,
                )
            }
        },
    ) { padding ->
        AppNavHost(
            navController = navController,
            startDestination = ZenSpendRoutes.SPLASH,
            modifier = Modifier.padding(padding),
            sessionState = sessionState,
            hasCompletedSetup = hasCompletedSetup,
        )
    }
}

@Composable
private fun SessionNavigationEffect(
    navController: NavHostController,
    sessionState: SessionState,
    hasCompletedOnboarding: Boolean,
    hasCompletedSetup: Boolean,
) {
    val minSplashDuration = 2000L
    val startTime = remember { System.currentTimeMillis() }
    LaunchedEffect(sessionState, hasCompletedOnboarding, hasCompletedSetup) {
        if (sessionState == SessionState.Loading) return@LaunchedEffect

        val elapsed = System.currentTimeMillis() - startTime
        val remainingDelay = minSplashDuration - elapsed
        if (remainingDelay > 0) {
            kotlinx.coroutines.delay(remainingDelay)
        }

        val target =
            when (sessionState) {
                SessionState.Loading -> return@LaunchedEffect
                SessionState.Authenticated -> {
                    if (hasCompletedSetup) HomeRoutes.GRAPH else SetupRoutes.GRAPH
                }
                SessionState.Unauthenticated,
                SessionState.Expired,
                -> {
                    if (hasCompletedOnboarding) AuthRoutes.GRAPH else OnboardingRoutes.GRAPH
                }
            }
        navController.navigate(target) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    sessionState: SessionState,
    hasCompletedSetup: Boolean,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(ZenSpendRoutes.SPLASH) {
            SplashScreen(sessionExpired = sessionState == SessionState.Expired)
        }
        onboardingGraph(
            navController = navController,
            onCompleted = {
                navController.navigate(AuthRoutes.GRAPH) {
                    popUpTo(OnboardingRoutes.GRAPH) { inclusive = true }
                    launchSingleTop = true
                }
            },
        )
        authGraph(
            navController = navController,
            onAuthenticated = {
                val target = if (hasCompletedSetup) HomeRoutes.GRAPH else SetupRoutes.GRAPH
                navController.navigate(target) {
                    popUpTo(AuthRoutes.GRAPH) { inclusive = true }
                    launchSingleTop = true
                }
            },
        )
        setupGraph(
            navController = navController,
            onCompleted = {
                navController.navigate(HomeRoutes.GRAPH) {
                    popUpTo(SetupRoutes.GRAPH) { inclusive = true }
                    launchSingleTop = true
                }
            },
        )
        homeGraph()
        profileScreen()
        settingsScreen()
    }
}

@Composable
private fun MainBottomBar(
    destinations: List<MainDestination>,
    currentRoute: String?,
    onDestinationClick: (MainDestination) -> Unit,
) {
    val context = LocalContext.current
    val homeDestination = destinations.find { it.selectedRoute == HomeRoutes.HOME }
    val profileDestination = destinations.find { it.selectedRoute == ProfileRoutes.PROFILE }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
    ) {
        // Main bottom bar surface
        Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            tonalElevation = 8.dp,
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MainNavigationTabs(
                    currentRoute = currentRoute,
                    homeDestination = homeDestination,
                    profileDestination = profileDestination,
                    onDestinationClick = onDestinationClick,
                    onPlaceholderClick = { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    },
                )
            }
        }
        AddTransactionFab(
            onClick = {
                Toast.makeText(context, "Add Transaction screen coming soon!", Toast.LENGTH_SHORT).show()
            },
        )
    }
}

@Composable
private fun MainNavigationTabs(
    currentRoute: String?,
    homeDestination: MainDestination?,
    profileDestination: MainDestination?,
    onDestinationClick: (MainDestination) -> Unit,
    onPlaceholderClick: (String) -> Unit,
) {
    MainTabItem(
        spec =
            MainTabSpec(
                label = "Home",
                selected = currentRoute == HomeRoutes.HOME,
                selectedIcon = Icons.Rounded.Home,
                unselectedIcon = Icons.Outlined.Home,
                destination = homeDestination,
            ),
        onDestinationClick = onDestinationClick,
    )
    BottomTabItem(
        label = "History",
        icon = Icons.AutoMirrored.Rounded.ReceiptLong,
        selected = false,
        onClick = { onPlaceholderClick("History screen coming soon!") },
        modifier = Modifier.weight(1f),
    )
    Spacer(modifier = Modifier.weight(1f))
    BottomTabItem(
        label = "Data",
        icon = Icons.Rounded.Analytics,
        selected = false,
        onClick = { onPlaceholderClick("Analytics screen coming soon!") },
        modifier = Modifier.weight(1f),
    )
    MainTabItem(
        spec =
            MainTabSpec(
                label = "Profile",
                selected = currentRoute == ProfileRoutes.PROFILE,
                selectedIcon = Icons.Rounded.Person,
                unselectedIcon = Icons.Outlined.Person,
                destination = profileDestination,
            ),
        onDestinationClick = onDestinationClick,
    )
}

@Composable
private fun MainTabItem(
    spec: MainTabSpec,
    onDestinationClick: (MainDestination) -> Unit,
) {
    BottomTabItem(
        label = spec.label,
        icon = if (spec.selected) spec.selectedIcon else spec.unselectedIcon,
        selected = spec.selected,
        onClick = { spec.destination?.let(onDestinationClick) },
        modifier = Modifier.weight(1f),
    )
}

@Composable
private fun BoxScope.AddTransactionFab(onClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-16).dp)
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background)
                .padding(4.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.AddCircle,
            contentDescription = "Add Transaction",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp),
        )
    }
}

@Composable
private fun BottomTabItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxHeight()
                .clickable(
                    onClick = onClick,
                    interactionSource = null,
                    indication = null, // Simple clean click
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (selected) {
            // Pill background active state
            Box(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp),
                    )
                    Text(
                        text = label,
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.Medium,
                            ),
                    )
                }
            }
        } else {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = label,
                style =
                    MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
            )
        }
    }
}

private fun NavHostController.navigateToMainDestination(destination: MainDestination) {
    navigate(destination.route) {
        popUpTo(HomeRoutes.GRAPH) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

private data class MainDestination(
    val route: String,
    val selectedRoute: String,
    val label: String,
    val icon: ImageVector,
)

private data class MainTabSpec(
    val label: String,
    val selected: Boolean,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val destination: MainDestination?,
)
