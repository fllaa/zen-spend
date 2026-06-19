package com.flla.zenspend

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.flla.zenspend.core.designsystem.component.CategorySelectionBottomSheet
import com.flla.zenspend.core.model.SessionState
import com.flla.zenspend.feature.analytics.AnalyticsRoutes
import com.flla.zenspend.feature.analytics.analyticsGraph
import com.flla.zenspend.feature.auth.AuthRoutes
import com.flla.zenspend.feature.auth.authGraph
import com.flla.zenspend.feature.history.HistoryRoutes
import com.flla.zenspend.feature.history.historyGraph
import com.flla.zenspend.feature.home.HomeRoutes
import com.flla.zenspend.feature.home.homeGraph
import com.flla.zenspend.feature.onboarding.OnboardingRoutes
import com.flla.zenspend.feature.onboarding.onboardingGraph
import com.flla.zenspend.feature.profile.ProfileRoutes
import com.flla.zenspend.feature.profile.profileScreen
import com.flla.zenspend.feature.settings.settingsScreen
import com.flla.zenspend.feature.setup.SetupRoutes
import com.flla.zenspend.feature.setup.setupGraph
import com.flla.zenspend.feature.transaction.navigateToTransactionDetails
import com.flla.zenspend.feature.transaction.transactionGraph
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
            route = HistoryRoutes.GRAPH,
            selectedRoute = HistoryRoutes.HISTORY,
            label = "History",
            icon = Icons.AutoMirrored.Rounded.ReceiptLong,
        ),
        MainDestination(
            route = AnalyticsRoutes.GRAPH,
            selectedRoute = AnalyticsRoutes.ANALYTICS,
            label = "Data",
            icon = Icons.Rounded.Analytics,
        ),
        MainDestination(
            route = ProfileRoutes.PROFILE,
            selectedRoute = ProfileRoutes.PROFILE,
            label = "Profile",
            icon = Icons.Rounded.Person,
        ),
    )

@OptIn(ExperimentalMaterial3Api::class)
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

    var showCategorySheet by rememberSaveable { mutableStateOf(false) }

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
                    onAddClick = { showCategorySheet = true },
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

        if (showCategorySheet) {
            CategorySelectionBottomSheet(
                onDismissRequest = { showCategorySheet = false },
                onCategorySelected = { categoryName, isIncome ->
                    showCategorySheet = false
                    navController.navigateToTransactionDetails(categoryName, isIncome)
                },
            )
        }
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
        enterTransition = { fadeIn(animationSpec = tween(100)) },
        exitTransition = { fadeOut(animationSpec = tween(100)) },
        popEnterTransition = { fadeIn(animationSpec = tween(100)) },
        popExitTransition = { fadeOut(animationSpec = tween(100)) },
    ) {
        registerRoutes(
            navController = navController,
            sessionState = sessionState,
            hasCompletedSetup = hasCompletedSetup,
        )
    }
}

@Suppress("LongMethod")
private fun NavGraphBuilder.registerRoutes(
    navController: NavHostController,
    sessionState: SessionState,
    hasCompletedSetup: Boolean,
) {
    composable(ZenSpendRoutes.SPLASH) {
        SplashScreen(sessionExpired = sessionState == SessionState.Expired)
    }
    onboardingGraph(
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
        onCompleted = {
            navController.navigate(HomeRoutes.GRAPH) {
                popUpTo(SetupRoutes.GRAPH) { inclusive = true }
                launchSingleTop = true
            }
        },
    )
    homeGraph()
    historyGraph()
    analyticsGraph(
        onNavigateToCategoryDetail = {
            navController.navigate(AnalyticsRoutes.CATEGORY_DETAIL)
        },
        onBackClick = {
            navController.popBackStack()
        },
    )
    profileScreen(
        onEditProfileClick = {
            navController.navigate(ProfileRoutes.EDIT_PROFILE)
        },
        onAccountsClick = {
            navController.navigate(ProfileRoutes.ACCOUNTS)
        },
        onCategoriesClick = {
            navController.navigate(ProfileRoutes.CATEGORIES)
        },
        onBudgetClick = {
            navController.navigate(ProfileRoutes.BUDGET)
        },
        onBackClick = {
            navController.popBackStack()
        },
    )
    settingsScreen()
    transactionGraph(
        onBackClick = { navController.popBackStack() },
        onSaveSuccess = { navController.popBackStack() },
    )
}

@Composable
private fun MainBottomBar(
    destinations: List<MainDestination>,
    currentRoute: String?,
    onDestinationClick: (MainDestination) -> Unit,
    onAddClick: () -> Unit,
) {
    val homeDestination = destinations.find { it.selectedRoute == HomeRoutes.HOME }
    val historyDestination = destinations.find { it.selectedRoute == HistoryRoutes.HISTORY }
    val analyticsDestination = destinations.find { it.selectedRoute == AnalyticsRoutes.ANALYTICS }
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
                    historyDestination = historyDestination,
                    analyticsDestination = analyticsDestination,
                    profileDestination = profileDestination,
                    onDestinationClick = onDestinationClick,
                )
            }
        }
        AddTransactionFab(
            onClick = onAddClick,
        )
    }
}

@Suppress("LongParameterList")
@Composable
private fun RowScope.MainNavigationTabs(
    currentRoute: String?,
    homeDestination: MainDestination?,
    historyDestination: MainDestination?,
    analyticsDestination: MainDestination?,
    profileDestination: MainDestination?,
    onDestinationClick: (MainDestination) -> Unit,
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
    MainTabItem(
        spec =
            MainTabSpec(
                label = "History",
                selected = currentRoute == HistoryRoutes.HISTORY,
                selectedIcon = Icons.AutoMirrored.Rounded.ReceiptLong,
                unselectedIcon = Icons.AutoMirrored.Rounded.ReceiptLong,
                destination = historyDestination,
            ),
        onDestinationClick = onDestinationClick,
    )
    Spacer(modifier = Modifier.weight(1f))
    MainTabItem(
        spec =
            MainTabSpec(
                label = "Data",
                selected = currentRoute == AnalyticsRoutes.ANALYTICS,
                selectedIcon = Icons.Rounded.Analytics,
                unselectedIcon = Icons.Rounded.Analytics,
                destination = analyticsDestination,
            ),
        onDestinationClick = onDestinationClick,
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
private fun RowScope.MainTabItem(
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
                    // Simple clean click.
                    indication = null,
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
