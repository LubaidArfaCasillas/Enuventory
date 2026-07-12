package dev.stefano.enuventory.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stefano.enuventory.R
import dev.stefano.enuventory.data.dummyInventoryItems
import dev.stefano.enuventory.ui.components.EnuBottomBar
import dev.stefano.enuventory.ui.components.EnuBottomBarItemData
import dev.stefano.enuventory.ui.components.EnuButton
import dev.stefano.enuventory.ui.components.EnuCategoryBadge
import dev.stefano.enuventory.ui.components.EnuCategoryBadgeState
import dev.stefano.enuventory.ui.components.EnuInventoryCard
import dev.stefano.enuventory.ui.components.EnuInventoryStatus
import dev.stefano.enuventory.ui.components.EnuSearchField
import dev.stefano.enuventory.ui.components.EnuTopBar
import dev.stefano.enuventory.ui.theme.EnuTheme

enum class HomeAdminState {
    Normal, Loading, Error, Empty
}

@Composable
fun HomeAdminPage(
    state: HomeAdminState,
    currentRoute: String?,
    onBottomBarItemClick: (EnuBottomBarItemData) -> Unit,
    onRetryClick: () -> Unit,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("All", "Elektro", "IoT")
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier,
        topBar = {
            EnuTopBar(
                title = "Home",
                showNotification = true,
                onNotificationClick = { }
            )
        },
        bottomBar = {
            EnuBottomBar(
                isAdmin = true,
                currentRoute = currentRoute,
                onItemClick = onBottomBarItemClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onFabClick,
                containerColor = EnuTheme.colors.backgroundBrandPrimaryStrongDefault,
                contentColor = EnuTheme.colors.contentBrandPrimaryOnStrong,
                shape = RoundedCornerShape(8.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        containerColor = EnuTheme.colors.surfaceDefaultBase
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            if (state != HomeAdminState.Error) {
                Spacer(modifier = Modifier.height(16.dp))

                EnuSearchField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search Placeholder"
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories.size) { index ->
                        val badgeState = if (state == HomeAdminState.Loading) {
                            EnuCategoryBadgeState.Loading
                        } else if (index == selectedCategoryIndex) {
                            EnuCategoryBadgeState.Selected
                        } else {
                            EnuCategoryBadgeState.Unselected
                        }

                        EnuCategoryBadge(
                            text = categories[index],
                            state = badgeState,
                            onClick = { selectedCategoryIndex = index }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            when (state) {
                HomeAdminState.Normal -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(dummyInventoryItems) { item ->
                            EnuInventoryCard(
                                title = item.title,
                                id = item.id,
                                stock = item.stock,
                                status = item.status
                            )
                        }
                    }
                }

                HomeAdminState.Loading -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(3) {
                            EnuInventoryCard(
                                title = "", id = "", stock = 0,
                                status = EnuInventoryStatus.Tersedia,
                                isLoading = true
                            )
                        }
                    }
                }

                HomeAdminState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_error),
                            contentDescription = "Error",
                            tint = EnuTheme.colors.contentSignalErrorDefault,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Terjadi Kesalahan",
                            style = EnuTheme.typography.ui.labels.normalCase.large,
                            color = EnuTheme.colors.contentDefaultPrimary
                        )
                        Text(
                            text = "error log",
                            style = EnuTheme.typography.ui.labels.normalCase.small,
                            color = EnuTheme.colors.contentSignalErrorDefault,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        EnuButton(
                            text = "Coba lagi",
                            onClick = onRetryClick,
                            modifier = Modifier.fillMaxWidth(0.6f)
                        )
                    }
                }

                HomeAdminState.Empty -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_info),
                            contentDescription = "Empty",
                            tint = EnuTheme.colors.contentBrandPrimaryDefault,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Belum ada asset yang ditambahkan",
                            style = EnuTheme.typography.ui.labels.normalCase.base,
                            color = EnuTheme.colors.contentDefaultPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Light")
@Composable
fun HomeAdminPageNormalPreviewLight() {
    EnuTheme {
        HomeAdminPage(
            state = HomeAdminState.Normal,
            currentRoute = "home",
            onBottomBarItemClick = {},
            onRetryClick = {},
            onFabClick = {}
        )
    }
}

@Preview(name = "Dark")
@Composable
fun HomeAdminPageNormalPreviewDark() {
    EnuTheme(darkTheme = true) {
        HomeAdminPage(
            state = HomeAdminState.Normal,
            currentRoute = "home",
            onBottomBarItemClick = {},
            onRetryClick = {},
            onFabClick = {}
        )
    }
}