package dev.stefano.enuventory

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.stefano.enuventory.ui.pages.HomeUserPage
import dev.stefano.enuventory.ui.pages.HomeUserState
import dev.stefano.enuventory.ui.theme.EnuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EnuTheme {
                var pageState by remember { mutableStateOf(HomeUserState.Normal) }

                var currentRoute by remember { mutableStateOf("home") }

                HomeUserPage(
                    state = pageState,
                    currentRoute = currentRoute,
                    onBottomBarItemClick = { menuItem ->
                        currentRoute = menuItem.route
                        Log.d("MainActivity", "Navigasi ke halaman: ${menuItem.label}")
                    },
                    onRetryClick = {
                        pageState = HomeUserState.Loading
                        Log.d(
                            "MainActivity",
                            "Tombol Coba Lagi diklik, mengubah state ke Loading..."
                        )
                    },
                    isAdmin = false
                )
            }
        }
    }
}