package dev.stefano.enuventory

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.stefano.enuventory.ui.pages.HistoryPage
import dev.stefano.enuventory.ui.pages.HistoryPageState
import dev.stefano.enuventory.ui.pages.HomeUserPage
import dev.stefano.enuventory.ui.pages.HomeUserState
import dev.stefano.enuventory.ui.pages.SettingsPage
import dev.stefano.enuventory.ui.theme.EnuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EnuTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                var homeState by remember { mutableStateOf(HomeUserState.Normal) }
                var historyState by remember { mutableStateOf(HistoryPageState.Normal) }

                val onBottomBarClick: (String) -> Unit = { targetRoute ->
                    if (currentRoute != targetRoute) {
                        navController.navigate(targetRoute) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(route = "home") {
                        HomeUserPage(
                            state = homeState,
                            currentRoute = currentRoute,
                            onBottomBarItemClick = { item -> onBottomBarClick(item.route) },
                            onRetryClick = {
                                homeState = HomeUserState.Loading
                                Log.d("Nav", "Home retry: Mengubah state ke Loading")
                            },
                            isAdmin = false
                        )
                    }

                    composable(route = "history") {
                        HistoryPage(
                            state = historyState,
                            currentRoute = currentRoute,
                            onBottomBarItemClick = { item -> onBottomBarClick(item.route) },
                            onRetryClick = {
                                historyState = HistoryPageState.Loading
                                Log.d("Nav", "History retry: Mengubah state ke Loading")
                            },
                            onDetailClick = { itemId ->
                                Log.d("Nav", "Membuka detail barang ID: $itemId")
                            },
                            isAdmin = false
                        )
                    }

                    composable(route = "settings") {
                        SettingsPage(
                            username = "Stefano",
                            role = "User",
                            appVersion = "v1.0.0",
                            currentRoute = currentRoute,
                            onBottomBarItemClick = { item -> onBottomBarClick(item.route) },
                            onSignOutClick = {
                                Log.d("Nav", "User clicked Sign Out!")
                            },
                            isAdmin = false
                        )
                    }
                }
            }
        }
    }
}