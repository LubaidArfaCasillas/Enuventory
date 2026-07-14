package dev.stefano.enuventory.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.stefano.enuventory.domain.model.UserRole
import dev.stefano.enuventory.ui.common.UiState
import dev.stefano.enuventory.ui.pages.ApprovalPage
import dev.stefano.enuventory.ui.pages.DetailAssetAdminPage
import dev.stefano.enuventory.ui.pages.DetailAssetUserPage
import dev.stefano.enuventory.ui.pages.DetailRequestPage
import dev.stefano.enuventory.ui.pages.DetailRiwayatPage
import dev.stefano.enuventory.ui.pages.EditAssetPage
import dev.stefano.enuventory.ui.pages.HistoryPage
import dev.stefano.enuventory.ui.pages.HomeAdminPage
import dev.stefano.enuventory.ui.pages.HomeUserPage
import dev.stefano.enuventory.ui.pages.PengembalianPage
import dev.stefano.enuventory.ui.pages.ScanQRPage
import dev.stefano.enuventory.ui.pages.SettingsAdminPage
import dev.stefano.enuventory.ui.pages.SettingsUserPage
import dev.stefano.enuventory.ui.pages.TambahAssetPage
import dev.stefano.enuventory.ui.pages.UploadBuktiFotoPage
import dev.stefano.enuventory.ui.screen.approval.ApprovalViewModel
import dev.stefano.enuventory.ui.screen.approval.DetailRequestViewModel
import dev.stefano.enuventory.ui.screen.asset.DetailAssetAdminViewModel
import dev.stefano.enuventory.ui.screen.asset.DetailAssetUserViewModel
import dev.stefano.enuventory.ui.screen.asset.EditAssetViewModel
import dev.stefano.enuventory.ui.screen.asset.TambahAssetViewModel
import dev.stefano.enuventory.ui.screen.auth.AuthViewModel
import dev.stefano.enuventory.ui.screen.auth.LoginScreen
import dev.stefano.enuventory.ui.screen.history.DetailRiwayatViewModel
import dev.stefano.enuventory.ui.screen.history.HistoryViewModel
import dev.stefano.enuventory.ui.screen.history.ReturnAssetViewModel
import dev.stefano.enuventory.ui.screen.home.HomeViewModel
import dev.stefano.enuventory.ui.screen.settings.SettingsViewModel
import dev.stefano.enuventory.ui.theme.EnuTheme

// Tab-tab di bottom bar dianggap "sejajar" (bukan drill-down), jadi transisinya
// cukup fade — slide terarah cuma dipakai untuk navigasi push/pop ke halaman detail.
private fun NavBackStackEntry.isTopLevel(): Boolean =
    destination.hasRoute<EnuRoute.Home>() ||
            destination.hasRoute<EnuRoute.History>() ||
            destination.hasRoute<EnuRoute.Approval>() ||
            destination.hasRoute<EnuRoute.Settings>()

// Durasi mengikuti "rhythm" motion design: gerakan utama lebih lama dari companion
// animation (fade/scale), dan exit lebih singkat dari enter karena user lebih notice
// kedatangan daripada kepergian.
private const val DURATION_LONG_MS = 500   // slide utama (enter & pop exit)
private const val DURATION_MEDIUM_MS = 400 // companion: fade & scale saat enter
private const val DURATION_SHORT_MS = 250  // exit forward (cepat, tidak jadi fokus)
private const val TAB_FADE_DURATION_MS = 300

private val EaseOutQuart = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)
private val EaseOutBack = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
private val EaseInOutCubic = CubicBezierEasing(0.65f, 0f, 0.35f, 1f)
private val EaseInCubic = CubicBezierEasing(0.32f, 0f, 0.67f, 0f)

/**
 * NavGraph utama aplikasi.
 *
 * - Otomatis routing ke home/login berdasarkan auth state dari AuthViewModel.
 * - isAdmin ditentukan dari currentUser.role — bukan dari parameter tersebar.
 * - Semua route type-safe via EnuRoute sealed interface.
 */
@Composable
fun EnuNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val isAdmin = currentUser?.role == UserRole.Admin

    // Helper: navigasi ke bottom bar item dengan popUpTo agar back stack bersih
    val onBottomBarClick: (EnuRoute) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    // Start destination berdasarkan auth state
    val startDestination: EnuRoute = if (currentUser != null) EnuRoute.Home else EnuRoute.Login

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            if (initialState.isTopLevel() && targetState.isTopLevel()) {
                fadeIn(animationSpec = tween(TAB_FADE_DURATION_MS))
            } else {
                // Masuk dari 50% lebar layar, sedikit "overshoot" di scale biar kerasa nempel.
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(DURATION_LONG_MS, easing = EaseOutQuart),
                    initialOffset = { fullOffset -> fullOffset / 2 }
                ) + fadeIn(
                    initialAlpha = 0.05f,
                    animationSpec = tween(DURATION_MEDIUM_MS, easing = LinearOutSlowInEasing)
                ) + scaleIn(
                    initialScale = 0.90f,
                    animationSpec = tween(DURATION_MEDIUM_MS, easing = EaseOutBack)
                )
            }
        },
        exitTransition = {
            if (initialState.isTopLevel() && targetState.isTopLevel()) {
                fadeOut(animationSpec = tween(TAB_FADE_DURATION_MS))
            } else {
                // Halaman lama cuma mundur 25% (asimetris dgn enter) & cepat karena bukan fokus user.
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(DURATION_SHORT_MS, easing = EaseInOutCubic),
                    targetOffset = { fullOffset -> fullOffset / 4 }
                ) + fadeOut(animationSpec = tween(DURATION_SHORT_MS)) +
                        scaleOut(targetScale = 0.96f, animationSpec = tween(DURATION_SHORT_MS))
            }
        },
        popEnterTransition = {
            // Balik: masuk dari kiri, scale-in ringan biar gak berasa "njedug".
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(DURATION_LONG_MS, easing = EaseOutQuart)
            ) + scaleIn(initialScale = 0.93f, animationSpec = tween(DURATION_MEDIUM_MS))
        },
        popExitTransition = {
            // Halaman yang ditinggalkan membesar dikit (1.05) untuk kesan "maju ke arah user".
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(DURATION_LONG_MS, easing = EaseInCubic)
            ) + scaleOut(targetScale = 1.05f, animationSpec = tween(DURATION_LONG_MS))
        }
    ) {

        // ── Auth ────────────────────────────────────────────────────────────
        composable<EnuRoute.Login> {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(EnuRoute.Home) {
                        popUpTo(EnuRoute.Login) { inclusive = true }
                    }
                }
            )
        }

        // ── Home (User / Admin) ─────────────────────────────────────────────
        composable<EnuRoute.Home> {
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            val homeViewModel: HomeViewModel = hiltViewModel()
            val assetsState by homeViewModel.assetsState.collectAsStateWithLifecycle()

            if (isAdmin) {
                HomeAdminPage(
                    state = assetsState,
                    currentRoute = currentRoute,
                    onBottomBarItemClick = { item ->
                        val route = when (item.route) {
                            "home" -> EnuRoute.Home
                            "approval" -> EnuRoute.Approval
                            "settings" -> EnuRoute.Settings
                            else -> EnuRoute.Home
                        }
                        onBottomBarClick(route)
                    },
                    onRetryClick = {},
                    onFabClick = { navController.navigate(EnuRoute.TambahAsset) },
                    onAssetClick = { assetId -> navController.navigate(EnuRoute.DetailAssetAdmin(assetId)) }
                )
            } else {
                HomeUserPage(
                    state = assetsState,
                    currentRoute = currentRoute,
                    onBottomBarItemClick = { item ->
                        val route = when (item.route) {
                            "home" -> EnuRoute.Home
                            "history" -> EnuRoute.History
                            "settings" -> EnuRoute.Settings
                            else -> EnuRoute.Home
                        }
                        onBottomBarClick(route)
                    },
                    onRetryClick = {},
                    onAssetClick = { assetId -> navController.navigate(EnuRoute.DetailAsset(assetId)) },
                    isAdmin = false
                )
            }
        }

        // ── History (User) ──────────────────────────────────────────────────
        composable<EnuRoute.History> {
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            val historyViewModel: HistoryViewModel = hiltViewModel()
            val historyState by historyViewModel.historyState.collectAsStateWithLifecycle()

            HistoryPage(
                state = historyState,
                currentRoute = currentRoute,
                onBottomBarItemClick = { item ->
                    val route = when (item.route) {
                        "home" -> EnuRoute.Home
                        "history" -> EnuRoute.History
                        "settings" -> EnuRoute.Settings
                        else -> EnuRoute.Home
                    }
                    onBottomBarClick(route)
                },
                onRetryClick = {},
                onDetailClick = { recordId ->
                    navController.navigate(EnuRoute.DetailRiwayat(recordId))
                },
                isAdmin = false
            )
        }

        // ── Settings (User / Admin) ─────────────────────────────────────────
        composable<EnuRoute.Settings> {
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val themeMode by settingsViewModel.themeMode.collectAsStateWithLifecycle()

            if (isAdmin) {
                SettingsAdminPage(
                    username = currentUser?.name ?: "",
                    role = "Admin",
                    appVersion = "v1.0.0",
                    currentRoute = currentRoute,
                    selectedTheme = themeMode,
                    onThemeSelected = { settingsViewModel.setThemeMode(it) },
                    onBottomBarItemClick = { item ->
                        val route = when (item.route) {
                            "home" -> EnuRoute.Home
                            "approval" -> EnuRoute.Approval
                            "settings" -> EnuRoute.Settings
                            else -> EnuRoute.Home
                        }
                        onBottomBarClick(route)
                    },
                    onSignOutClick = { settingsViewModel.signOut() },
                    isAdmin = true
                )
            } else {
                SettingsUserPage(
                    username = currentUser?.name ?: "",
                    role = "User",
                    appVersion = "v1.0.0",
                    currentRoute = currentRoute,
                    selectedTheme = themeMode,
                    onThemeSelected = { settingsViewModel.setThemeMode(it) },
                    onBottomBarItemClick = { item ->
                        val route = when (item.route) {
                            "home" -> EnuRoute.Home
                            "history" -> EnuRoute.History
                            "settings" -> EnuRoute.Settings
                            else -> EnuRoute.Home
                        }
                        onBottomBarClick(route)
                    },
                    onSignOutClick = { settingsViewModel.signOut() },
                    isAdmin = false
                )
            }
        }

        // ── Approval (Admin) ────────────────────────────────────────────────
        composable<EnuRoute.Approval> {
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            val approvalViewModel: ApprovalViewModel = hiltViewModel()
            val requestsState by approvalViewModel.requestsState.collectAsStateWithLifecycle()

            ApprovalPage(
                state = requestsState,
                currentRoute = currentRoute,
                onBottomBarItemClick = { item ->
                    val route = when (item.route) {
                        "home" -> EnuRoute.Home
                        "approval" -> EnuRoute.Approval
                        "settings" -> EnuRoute.Settings
                        else -> EnuRoute.Home
                    }
                    onBottomBarClick(route)
                },
                onRetryClick = {},
                onDetailClick = { recordId ->
                    navController.navigate(EnuRoute.DetailRequest(recordId))
                },
                isAdmin = true
            )
        }

        // ── Tambah Asset (Admin) ────────────────────────────────────────────
        composable<EnuRoute.TambahAsset> {
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            val tambahAssetViewModel: TambahAssetViewModel = hiltViewModel()
            val addState by tambahAssetViewModel.addState.collectAsStateWithLifecycle()

            TambahAssetPage(
                state = addState,
                currentRoute = currentRoute,
                onBottomBarItemClick = { item ->
                    val route = when (item.route) {
                        "home" -> EnuRoute.Home
                        "approval" -> EnuRoute.Approval
                        "settings" -> EnuRoute.Settings
                        else -> EnuRoute.Home
                    }
                    onBottomBarClick(route)
                },
                onBackClick = { navController.popBackStack() },
                onTambahAssetClick = { title, stock, status, category, description, imageBytes ->
                    tambahAssetViewModel.addAsset(
                        title = title,
                        stockStr = stock,
                        statusStr = status,
                        category = category,
                        description = description,
                        imageBytes = imageBytes,
                        onSuccess = { navController.popBackStack() }
                    )
                },
                onRetryClick = { tambahAssetViewModel.resetState() }
            )
        }

        // ── Detail Asset (User) ─────────────────────────────────────────────
        composable<EnuRoute.DetailAsset> { backStackEntry ->
            val route = backStackEntry.toRoute<EnuRoute.DetailAsset>()
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            val detailAssetUserViewModel: DetailAssetUserViewModel = hiltViewModel()
            val uiState by detailAssetUserViewModel.uiState.collectAsStateWithLifecycle()

            DetailAssetUserPage(
                state = uiState,
                currentRoute = currentRoute,
                onBottomBarItemClick = { item ->
                    val navRoute = when (item.route) {
                        "home" -> EnuRoute.Home
                        "history" -> EnuRoute.History
                        "settings" -> EnuRoute.Settings
                        else -> EnuRoute.Home
                    }
                    onBottomBarClick(navRoute)
                },
                onBackClick = { navController.popBackStack() },
                onPinjamClick = { returnEstimate ->
                    detailAssetUserViewModel.requestBorrow(returnEstimate)
                },
                onBatalkanClick = { recordId ->
                    detailAssetUserViewModel.cancelBorrow(recordId)
                },
                onRetryClick = {}
            )
        }

        // ── Detail Asset (Admin) ────────────────────────────────────────────
        composable<EnuRoute.DetailAssetAdmin> { backStackEntry ->
            val route = backStackEntry.toRoute<EnuRoute.DetailAssetAdmin>()
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            val detailAssetAdminViewModel: DetailAssetAdminViewModel = hiltViewModel()
            val assetState by detailAssetAdminViewModel.assetState.collectAsStateWithLifecycle()

            DetailAssetAdminPage(
                state = assetState,
                currentRoute = currentRoute,
                onBottomBarItemClick = { item ->
                    val navRoute = when (item.route) {
                        "home" -> EnuRoute.Home
                        "approval" -> EnuRoute.Approval
                        "settings" -> EnuRoute.Settings
                        else -> EnuRoute.Home
                    }
                    onBottomBarClick(navRoute)
                },
                onBackClick = { navController.popBackStack() },
                onEditClick = { navController.navigate(EnuRoute.EditAsset(route.assetId)) },
                onHapusClick = {
                    detailAssetAdminViewModel.deleteAsset(onSuccess = { navController.popBackStack() })
                },
                onRetryClick = {}
            )
        }

        // ── Edit Asset (Admin) ──────────────────────────────────────────────
        composable<EnuRoute.EditAsset> {
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            val editAssetViewModel: EditAssetViewModel = hiltViewModel()
            val assetState by editAssetViewModel.assetState.collectAsStateWithLifecycle()
            val saveState by editAssetViewModel.saveState.collectAsStateWithLifecycle()

            EditAssetPage(
                assetState = assetState,
                saveState = saveState,
                currentRoute = currentRoute,
                onBottomBarItemClick = { item ->
                    val navRoute = when (item.route) {
                        "home" -> EnuRoute.Home
                        "approval" -> EnuRoute.Approval
                        "settings" -> EnuRoute.Settings
                        else -> EnuRoute.Home
                    }
                    onBottomBarClick(navRoute)
                },
                onBackClick = { navController.popBackStack() },
                onEditAssetClick = { title, stock, status, category, description, imageBytes ->
                    editAssetViewModel.editAsset(
                        title = title,
                        stockStr = stock,
                        statusStr = status,
                        category = category,
                        description = description,
                        imageBytes = imageBytes,
                        onSuccess = { navController.popBackStack() }
                    )
                },
                onRetryClick = { editAssetViewModel.resetState() }
            )
        }

        // ── Detail Riwayat (User) ───────────────────────────────────────────
        composable<EnuRoute.DetailRiwayat> { backStackEntry ->
            val route = backStackEntry.toRoute<EnuRoute.DetailRiwayat>()
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            val detailRiwayatViewModel: DetailRiwayatViewModel = hiltViewModel()
            val uiState by detailRiwayatViewModel.uiState.collectAsStateWithLifecycle()

            DetailRiwayatPage(
                state = uiState,
                currentRoute = currentRoute,
                onBottomBarItemClick = { item ->
                    val navRoute = when (item.route) {
                        "home" -> EnuRoute.Home
                        "history" -> EnuRoute.History
                        "settings" -> EnuRoute.Settings
                        else -> EnuRoute.Home
                    }
                    onBottomBarClick(navRoute)
                },
                onBackClick = { navController.popBackStack() },
                onScanQrClick = { navController.navigate(EnuRoute.ScanQR) },
                onKembalikanClick = {
                    navController.navigate(EnuRoute.Pengembalian(route.recordId))
                },
                onRetryClick = { detailRiwayatViewModel.loadRecord() }
            )
        }

        // ── Detail Request (Admin) ──────────────────────────────────────────
        composable<EnuRoute.DetailRequest> { backStackEntry ->
            val route = backStackEntry.toRoute<EnuRoute.DetailRequest>()
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            val detailRequestViewModel: DetailRequestViewModel = hiltViewModel()
            val uiState by detailRequestViewModel.uiState.collectAsStateWithLifecycle()

            DetailRequestPage(
                state = uiState,
                currentRoute = currentRoute,
                onBottomBarItemClick = { item ->
                    val navRoute = when (item.route) {
                        "home" -> EnuRoute.Home
                        "approval" -> EnuRoute.Approval
                        "settings" -> EnuRoute.Settings
                        else -> EnuRoute.Home
                    }
                    onBottomBarClick(navRoute)
                },
                onBackClick = { navController.popBackStack() },
                onApproveClick = {
                    detailRequestViewModel.approveRequest(onSuccess = { navController.popBackStack() })
                },
                onTolakClick = {
                    detailRequestViewModel.rejectRequest(onSuccess = { navController.popBackStack() })
                },
                onRetryClick = { detailRequestViewModel.loadRecord() }
            )
        }

        // ── Scan QR ─────────────────────────────────────────────────────────
        composable<EnuRoute.ScanQR> {
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            ScanQRPage(
                state = dev.stefano.enuventory.ui.pages.ScanQRState.Scanning,
                currentRoute = currentRoute,
                onBottomBarItemClick = {},
                onBackClick = { navController.popBackStack() },
                onUlangiClick = {},
                onKonfirmasiYaClick = { navController.popBackStack() }
            )
        }

        // ── Upload Bukti Foto ────────────────────────────────────────────────
        composable<EnuRoute.UploadBuktiFoto> { backStackEntry ->
            val route = backStackEntry.toRoute<EnuRoute.UploadBuktiFoto>()
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            val returnAssetViewModel: ReturnAssetViewModel = hiltViewModel()
            val recordState by returnAssetViewModel.recordState.collectAsStateWithLifecycle()
            val uploadState by returnAssetViewModel.uploadState.collectAsStateWithLifecycle()

            when (val state = recordState) {
                is UiState.Success -> {
                    val record = state.data
                    UploadBuktiFotoPage(
                        state = uploadState,
                        assetTitle = record.assetTitle,
                        assetId = record.assetId,
                        timestamp = record.borrowDate,
                        currentRoute = currentRoute,
                        onBottomBarItemClick = { item ->
                            val navRoute = when (item.route) {
                                "home" -> EnuRoute.Home
                                "history" -> EnuRoute.History
                                "settings" -> EnuRoute.Settings
                                else -> EnuRoute.Home
                            }
                            onBottomBarClick(navRoute)
                        },
                        onBackClick = { navController.popBackStack() },
                        onCaptureClick = { returnAssetViewModel.onCapturePhoto() },
                        onUlangiClick = { returnAssetViewModel.onUlangiCapture() },
                        onSubmitClick = {
                            returnAssetViewModel.submitReturn(onSuccess = {
                                navController.navigate(EnuRoute.History) {
                                    popUpTo(EnuRoute.History) { inclusive = true }
                                }
                            })
                        }
                    )
                }
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = EnuTheme.colors.contentBrandPrimaryDefault)
                    }
                }
                else -> {
                    // Fallback
                }
            }
        }

        // ── Pengembalian ─────────────────────────────────────────────────────
        composable<EnuRoute.Pengembalian> { backStackEntry ->
            val route = backStackEntry.toRoute<EnuRoute.Pengembalian>()
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            val returnAssetViewModel: ReturnAssetViewModel = hiltViewModel()
            val recordState by returnAssetViewModel.recordState.collectAsStateWithLifecycle()

            when (val state = recordState) {
                is UiState.Success -> {
                    val record = state.data
                    PengembalianPage(
                        assetTitle = record.assetTitle,
                        assetId = record.assetId,
                        currentRoute = currentRoute,
                        onBottomBarItemClick = { item ->
                            val navRoute = when (item.route) {
                                "home" -> EnuRoute.Home
                                "history" -> EnuRoute.History
                                "settings" -> EnuRoute.Settings
                                else -> EnuRoute.Home
                            }
                            onBottomBarClick(navRoute)
                        },
                        onBackClick = { navController.popBackStack() },
                        onUploadBuktiClick = {
                            navController.navigate(EnuRoute.UploadBuktiFoto(route.recordId))
                        }
                    )
                }
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = EnuTheme.colors.contentBrandPrimaryDefault)
                    }
                }
                else -> {
                    // Fallback
                }
            }
        }
    }
}
