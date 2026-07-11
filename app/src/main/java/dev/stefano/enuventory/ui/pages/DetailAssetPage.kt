package dev.stefano.enuventory.ui.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stefano.enuventory.R
import dev.stefano.enuventory.ui.components.EnuBorrowDialog
import dev.stefano.enuventory.ui.components.EnuBottomBar
import dev.stefano.enuventory.ui.components.EnuBottomBarItemData
import dev.stefano.enuventory.ui.components.EnuButton
import dev.stefano.enuventory.ui.components.EnuButtonVariant
import dev.stefano.enuventory.ui.components.EnuInventoryStatus
import dev.stefano.enuventory.ui.components.EnuInventoryStatusBadge
import dev.stefano.enuventory.ui.components.EnuTopBar
import dev.stefano.enuventory.ui.theme.EnuTheme

enum class DetailAssetState {
    Normal, Error, MenungguPersetujuan, SedangDipinjam
}

@Composable
fun DetailAssetPage(
    state: DetailAssetState,
    title: String,
    id: String,
    stock: Int,
    status: EnuInventoryStatus,
    description: String,
    currentRoute: String?,
    onBottomBarItemClick: (EnuBottomBarItemData) -> Unit,
    onBackClick: () -> Unit,
    onPinjamClick: () -> Unit,
    onBatalkanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showBorrowDialog by remember { mutableStateOf(false) }
    var isDialogSubmitting by remember { mutableStateOf(false) }

    if (showBorrowDialog) {
        EnuBorrowDialog(
            onDismissRequest = { showBorrowDialog = false },
            isSubmitting = isDialogSubmitting,
            onSubmitClick = { pesan, estimasi ->
                isDialogSubmitting = true
            }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            EnuTopBar(
                title = "Detail Asset",
                showBack = true,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            EnuBottomBar(
                isAdmin = false,
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
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color(0xFFB0B0B0))
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .align(Alignment.BottomCenter),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = EnuTheme.colors.surfaceDefaultBase),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, EnuTheme.colors.borderDefaultMedium)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = title,
                            style = EnuTheme.typography.content.headings.h3,
                            color = EnuTheme.colors.contentDefaultPrimary
                        )
                        Text(
                            text = "ID: $id",
                            style = EnuTheme.typography.content.headings.h6,
                            color = EnuTheme.colors.contentDefaultSubtle
                        )
                        Text(
                            text = "Stock: $stock",
                            style = EnuTheme.typography.content.headings.h6,
                            color = EnuTheme.colors.contentDefaultSubtle
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        EnuInventoryStatusBadge(status = status)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = EnuTheme.colors.surfaceDefaultBase),
                    border = BorderStroke(1.dp, EnuTheme.colors.borderDefaultMedium)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Deskripsi",
                            style = EnuTheme.typography.content.headings.h3,
                            color = EnuTheme.colors.contentDefaultPrimary
                        )
                        Text(
                            text = description,
                            style = EnuTheme.typography.content.body.medium,
                            color = EnuTheme.colors.contentDefaultPrimary
                        )
                    }
                }

                when (state) {
                    DetailAssetState.Normal -> {
                        EnuButton(
                            text = "Pinjam Asset",
                            onClick = { showBorrowDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    DetailAssetState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            EnuButton(
                                text = "Pinjam Asset",
                                onClick = { showBorrowDialog = true },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_error),
                                    contentDescription = "Error",
                                    tint = EnuTheme.colors.contentSignalErrorDefault,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Terjadi Kesalahan",
                                    style = EnuTheme.typography.ui.labels.normalCase.large,
                                    color = EnuTheme.colors.contentDefaultPrimary
                                )
                                Text(
                                    text = "error log",
                                    style = EnuTheme.typography.ui.labels.normalCase.small,
                                    color = EnuTheme.colors.contentSignalErrorDefault
                                )
                            }
                        }
                    }

                    DetailAssetState.MenungguPersetujuan -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Menunggu Persetujuan",
                                    style = EnuTheme.typography.ui.labels.normalCase.large,
                                    color = EnuTheme.colors.contentSignalWarningDefault,
                                    textAlign = TextAlign.Center
                                )
                            }

                            EnuButton(
                                text = "Batalkan",
                                variant = EnuButtonVariant.Danger,
                                onClick = onBatalkanClick,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    DetailAssetState.SedangDipinjam -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sedang Dipinjam",
                                style = EnuTheme.typography.ui.labels.normalCase.large,
                                color = EnuTheme.colors.contentSignalSuccessDefault,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun DetailAssetNormalPreview() {
    EnuTheme {
        DetailAssetPage(
            state = DetailAssetState.Normal,
            title = "Title", id = "HW-0018-A", stock = 5,
            status = EnuInventoryStatus.Tersedia,
            description = "Lorem Ipsum Dolor Sit Amet",
            currentRoute = "home", onBottomBarItemClick = {},
            onBackClick = {}, onPinjamClick = {}, onBatalkanClick = {}
        )
    }
}

@Preview
@Composable
fun DetailAssetErrorPreview() {
    EnuTheme {
        DetailAssetPage(
            state = DetailAssetState.Error,
            title = "Title", id = "HW-0018-A", stock = 5,
            status = EnuInventoryStatus.Tersedia,
            description = "Lorem Ipsum Dolor Sit Amet",
            currentRoute = "home", onBottomBarItemClick = {},
            onBackClick = {}, onPinjamClick = {}, onBatalkanClick = {}
        )
    }
}

@Preview
@Composable
fun DetailAssetWaitingPreview() {
    EnuTheme {
        DetailAssetPage(
            state = DetailAssetState.MenungguPersetujuan,
            title = "Title", id = "HW-0018-A", stock = 5,
            status = EnuInventoryStatus.Tersedia,
            description = "Lorem Ipsum Dolor Sit Amet",
            currentRoute = "home", onBottomBarItemClick = {},
            onBackClick = {}, onPinjamClick = {}, onBatalkanClick = {}
        )
    }
}

@Preview
@Composable
fun DetailAssetBorrowedPreview() {
    EnuTheme {
        DetailAssetPage(
            state = DetailAssetState.SedangDipinjam,
            title = "Title", id = "HW-0018-A", stock = 5,
            status = EnuInventoryStatus.Tersedia,
            description = "Lorem Ipsum Dolor Sit Amet",
            currentRoute = "home", onBottomBarItemClick = {},
            onBackClick = {}, onPinjamClick = {}, onBatalkanClick = {}
        )
    }
}