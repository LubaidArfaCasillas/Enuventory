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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stefano.enuventory.R
import dev.stefano.enuventory.data.dummyBorrowRecords
import dev.stefano.enuventory.domain.model.BorrowStatus
import dev.stefano.enuventory.ui.components.EnuBorrowStatus
import dev.stefano.enuventory.ui.util.toUiStatus
import dev.stefano.enuventory.ui.components.EnuBottomBar
import dev.stefano.enuventory.ui.components.EnuBottomBarItemData
import dev.stefano.enuventory.ui.components.EnuButton
import dev.stefano.enuventory.ui.components.EnuHistoryCard
import dev.stefano.enuventory.ui.components.EnuTab
import dev.stefano.enuventory.ui.components.EnuTopBar
import dev.stefano.enuventory.ui.theme.EnuTheme

enum class ApprovalPageState {
    Normal, Loading, Error, Empty
}

@Composable
fun ApprovalPage(
    state: ApprovalPageState,
    currentRoute: String?,
    onBottomBarItemClick: (EnuBottomBarItemData) -> Unit,
    onRetryClick: () -> Unit,
    onDetailClick: (id: String) -> Unit,
    modifier: Modifier = Modifier,
    isAdmin: Boolean = false
) {
    val tabTitles = listOf("Aktif", "Selesai")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val filteredItems = remember(selectedTabIndex) {
        dummyBorrowRecords.filter { item ->
            if (selectedTabIndex == 0) {
                item.status == BorrowStatus.Pending
            } else {
                item.status == BorrowStatus.Completed || item.status == BorrowStatus.Rejected
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            EnuTopBar(
                title = "Approval",
                showNotification = false
            )
        },
        bottomBar = {
            EnuBottomBar(
                isAdmin = isAdmin,
                currentRoute = currentRoute,
                onItemClick = onBottomBarItemClick
            )
        },
        containerColor = EnuTheme.colors.surfaceDefaultBase
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            EnuTab(
                tabs = tabTitles,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            when (state) {
                ApprovalPageState.Normal -> {
                    if (filteredItems.isEmpty()) {
                        ApprovalEmptyState()
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredItems) { item ->
                                EnuHistoryCard(
                                    title = item.assetTitle,
                                    id = item.assetId,
                                    stock = item.assetStock,
                                    status = item.status.toUiStatus(),
                                    borrowDate = item.borrowDate,
                                    returnEstimate = if (item.isFinished) {
                                        item.returnDate ?: "-"
                                    } else {
                                        item.returnEstimate
                                    },
                                    isFinished = item.isFinished,
                                    onDetailClick = { onDetailClick(item.id) }
                                )
                            }
                        }
                    }
                }

                ApprovalPageState.Loading -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(2) {
                            EnuHistoryCard(
                                title = "", id = "", stock = 0,
                                status = EnuBorrowStatus.Menunggu,
                                borrowDate = "", returnEstimate = "",
                                onDetailClick = {},
                                isLoading = true
                            )
                        }
                    }
                }

                ApprovalPageState.Error -> {
                    ApprovalErrorState(onRetryClick = onRetryClick)
                }

                ApprovalPageState.Empty -> {
                    ApprovalEmptyState()
                }
            }
        }
    }
}

@Composable
private fun ApprovalEmptyState() {
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
            text = "Belum ada riwayat",
            style = EnuTheme.typography.ui.labels.normalCase.base,
            color = EnuTheme.colors.contentDefaultPrimary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ApprovalErrorState(onRetryClick: () -> Unit) {
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

@Preview(showBackground = true, name = "Light")
@Composable
fun ApprovalPageNormalPreviewLight() {
    EnuTheme {
        ApprovalPage(
            state = ApprovalPageState.Normal,
            currentRoute = "approval",
            onBottomBarItemClick = {},
            onRetryClick = {},
            onDetailClick = {},
            isAdmin = true
        )
    }
}

@Preview(name = "Dark")
@Composable
fun ApprovalPageNormalPreviewDark() {
    EnuTheme(darkTheme = true) {
        ApprovalPage(
            state = ApprovalPageState.Normal,
            currentRoute = "approval",
            onBottomBarItemClick = {},
            onRetryClick = {},
            onDetailClick = {},
            isAdmin = true
        )
    }
}