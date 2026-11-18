package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.model.NotificationCategory
import com.example.ui.components.DynamicNotchOverlay
import com.example.ui.components.GlassNotificationDrawer
import com.example.ui.components.InteractiveOnboardingOverlay
import com.example.ui.components.UpdateProgressSheet
import com.example.ui.components.WhatsNewDialog
import com.example.ui.screens.DocsScreen
import com.example.ui.screens.DrawerFeedScreen
import com.example.ui.screens.NotchStudioScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.NovaIslandTheme
import com.example.viewmodel.NovaIslandViewModel

private const val ROUTE_STUDIO = "studio"
private const val ROUTE_DRAWER = "drawer"
private const val ROUTE_SETTINGS = "settings"
private const val ROUTE_DOCS = "docs"

private data class NavTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: NovaIslandViewModel = viewModel()
            NovaIslandApp(viewModel = viewModel)
        }
    }
}

@Composable
fun NovaIslandApp(viewModel: NovaIslandViewModel) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val activeNotch by viewModel.activeNotchNotification.collectAsStateWithLifecycle()
    val notchState by viewModel.notchVisualState.collectAsStateWithLifecycle()
    val isDrawerOpen by viewModel.isDrawerOpen.collectAsStateWithLifecycle()
    val quickToggles by viewModel.quickToggles.collectAsStateWithLifecycle()
    val backups by viewModel.backups.collectAsStateWithLifecycle()
    val updatePhase by viewModel.updatePhase.collectAsStateWithLifecycle()
    val showUpdateSheet by viewModel.showUpdateSheet.collectAsStateWithLifecycle()
    val showWhatsNew by viewModel.showWhatsNewDialog.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val isNotificationAccessGranted by viewModel.isNotificationAccessGranted.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshSystemPermissions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(statusBanner) {
        statusBanner?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusBanner()
        }
    }

    var docsInitialTab by remember { mutableIntStateOf(0) }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ROUTE_STUDIO

    val tabs = remember {
        listOf(
            NavTab(ROUTE_STUDIO, "Studio", Icons.Rounded.AutoAwesome, Icons.Outlined.AutoAwesome),
            NavTab(ROUTE_DRAWER, "Drawer", Icons.Rounded.Layers, Icons.Outlined.Layers),
            NavTab(ROUTE_SETTINGS, "Pengaturan", Icons.Rounded.Tune, Icons.Outlined.Tune),
            NavTab(ROUTE_DOCS, "Panduan", Icons.Rounded.MenuBook, Icons.Outlined.MenuBook)
        )
    }

    fun navigateToTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    NovaIslandTheme(
        themeMode = settings.themeMode,
        accentHex = settings.accentHex
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useNavigationRail = maxWidth >= 600.dp

            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    bottomBar = {
                        if (!useNavigationRail) {
                            NavigationBar(
                                windowInsets = WindowInsets.navigationBars
                            ) {
                                tabs.forEach { tab ->
                                    val selected = currentRoute == tab.route
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = { navigateToTab(tab.route) },
                                        icon = {
                                            Icon(
                                                imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                                contentDescription = tab.label
                                            )
                                        },
                                        label = { Text(tab.label) },
                                        modifier = Modifier.testTag("nav_${tab.route}")
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (useNavigationRail) {
                            NavigationRail(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .safeDrawingPadding()
                                    .padding(top = 56.dp)
                            ) {
                                tabs.forEach { tab ->
                                    val selected = currentRoute == tab.route
                                    NavigationRailItem(
                                        selected = selected,
                                        onClick = { navigateToTab(tab.route) },
                                        icon = {
                                            Icon(
                                                imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                                contentDescription = tab.label
                                            )
                                        },
                                        label = { Text(tab.label) },
                                        modifier = Modifier.testTag("rail_${tab.route}")
                                    )
                                }
                            }
                        }

                        NavHost(
                            navController = navController,
                            startDestination = ROUTE_STUDIO,
                            modifier = Modifier.weight(1f)
                        ) {
                            composable(ROUTE_STUDIO) {
                                NotchStudioScreen(
                                    settings = settings,
                                    activeNotification = activeNotch,
                                    notificationCount = notifications.size,
                                    isOnline = isOnline,
                                    isNotificationAccessGranted = isNotificationAccessGranted,
                                    onTriggerPreset = viewModel::triggerPresetNotification,
                                    onOpenDrawer = viewModel::openDrawer,
                                    onCheckUpdate = { viewModel.checkGitHubUpdate(openSheet = true) },
                                    onOpenSettingsTab = { navigateToTab(ROUTE_SETTINGS) },
                                    onSendTestPushNotification = viewModel::sendTestPushNotification
                                )
                            }

                            composable(ROUTE_DRAWER) {
                                DrawerFeedScreen(
                                    notifications = notifications,
                                    onOpenGlassDrawer = viewModel::openDrawer,
                                    onSelectInNotch = viewModel::selectNotificationInNotch,
                                    onDismissNotification = viewModel::dismissNotification,
                                    onClearAll = viewModel::clearAllNotifications
                                )
                            }

                            composable(ROUTE_SETTINGS) {
                                SettingsScreen(
                                    settings = settings,
                                    backups = backups,
                                    isNotificationAccessGranted = isNotificationAccessGranted,
                                    onUpdateSettings = viewModel::updateSettings,
                                    onResetDefaults = viewModel::resetNotchDefaults,
                                    onCheckUpdate = { viewModel.checkGitHubUpdate(openSheet = true) },
                                    onCreateBackup = viewModel::createManualBackup,
                                    onRestoreBackup = viewModel::restoreFromBackup,
                                    onDeleteBackup = viewModel::deleteBackup
                                )
                            }

                            composable(ROUTE_DOCS) {
                                DocsScreen(
                                    initialSection = docsInitialTab,
                                    onReplayTutorial = viewModel::replayOnboarding,
                                    onOpenWhatsNew = viewModel::openWhatsNew
                                )
                            }
                        }
                    }
                }

                // Floating iPhone 18 Dynamic Notch Overlay at the status bar area
                DynamicNotchOverlay(
                    notification = activeNotch,
                    visualState = notchState,
                    settings = settings,
                    onNotchTap = viewModel::onNotchTapped,
                    onCollapse = viewModel::collapseNotchToCompact,
                    onOpenDrawer = viewModel::openDrawer,
                    onPrimaryAction = { item ->
                        when (item.category) {
                            NotificationCategory.MUSIC -> viewModel.toggleMusicPlayback()
                            NotificationCategory.UPDATE -> viewModel.checkGitHubUpdate(openSheet = true)
                            else -> viewModel.openDrawer()
                        }
                    },
                    onDismissNotification = viewModel::dismissNotification,
                    modifier = Modifier.align(Alignment.TopCenter)
                )

                // iPhone-style Glassmorphism Notification Drawer Overlay
                GlassNotificationDrawer(
                    visible = isDrawerOpen,
                    notifications = notifications,
                    quickToggles = quickToggles,
                    settings = settings,
                    isOnline = isOnline,
                    onToggleQuickControl = viewModel::toggleQuickControl,
                    onSelectNotification = viewModel::selectNotificationInNotch,
                    onDismissNotification = viewModel::dismissNotification,
                    onClearAll = viewModel::clearAllNotifications,
                    onClose = viewModel::closeDrawer
                )

                // Interactive First-Launch Onboarding Tutorial
                if (!settings.hasCompletedOnboarding) {
                    InteractiveOnboardingOverlay(
                        onTryNotchDemo = {
                            viewModel.triggerPresetNotification(NotificationCategory.MUSIC)
                        },
                        onTryDrawerDemo = {
                            viewModel.openDrawer()
                        },
                        onComplete = viewModel::completeOnboarding
                    )
                }

                // What's New Dialog
                WhatsNewDialog(
                    visible = showWhatsNew,
                    onOpenChangelog = {
                        docsInitialTab = 1
                        navigateToTab(ROUTE_DOCS)
                    },
                    onDismiss = viewModel::dismissWhatsNew
                )

                // GitHub Update Progress & SHA-256 Verification Sheet
                UpdateProgressSheet(
                    visible = showUpdateSheet,
                    phase = updatePhase,
                    repoLabel = "${settings.githubRepoOwner}/${settings.githubRepoName}",
                    onRetryCheck = { viewModel.checkGitHubUpdate(openSheet = true) },
                    onStartUpdate = viewModel::startUpdatePipeline,
                    onRunLocalVerificationPreview = viewModel::runLocalVerificationPipelinePreview,
                    onDismiss = viewModel::closeUpdateSheet
                )
            }
        }
    }
}
