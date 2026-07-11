package dev.stefano.enuventory.ui.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stefano.enuventory.ui.components.EnuBottomBar
import dev.stefano.enuventory.ui.components.EnuBottomBarItemData
import dev.stefano.enuventory.ui.components.EnuButton
import dev.stefano.enuventory.ui.components.EnuDetailHistoryCard
import dev.stefano.enuventory.ui.components.EnuTimeline
import dev.stefano.enuventory.ui.components.EnuTimelineItemData
import dev.stefano.enuventory.ui.components.EnuTimelineNodeStatus
import dev.stefano.enuventory.ui.components.EnuTopBar
import dev.stefano.enuventory.ui.theme.EnuTheme

enum class DetailRiwayatState {
    MenungguPersetujuan, MenungguPengambilan, BatasKembali, Dikembalikan
}

@Composable
fun DetailRiwayatPage(
    state: DetailRiwayatState,
    assetTitle: String,
    assetId: String,
    currentRoute: String?,
    onBottomBarItemClick: (EnuBottomBarItemData) -> Unit,
    onBackClick: () -> Unit,
    onScanQrClick: () -> Unit,
    onKembalikanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timelineItems = remember(state) {
        when (state) {
            DetailRiwayatState.MenungguPersetujuan -> listOf(
                EnuTimelineItemData(
                    "Diajukan",
                    "15 Okt 2026, 09:00",
                    EnuTimelineNodeStatus.Completed
                ),
                EnuTimelineItemData("Menunggu Persetujuan", null, EnuTimelineNodeStatus.Current),
                EnuTimelineItemData("Diambil", null, EnuTimelineNodeStatus.Upcoming),
                EnuTimelineItemData("Batas Kembali", null, EnuTimelineNodeStatus.Upcoming)
            )

            DetailRiwayatState.MenungguPengambilan -> listOf(
                EnuTimelineItemData(
                    "Diajukan",
                    "15 Okt 2026, 09:00",
                    EnuTimelineNodeStatus.Completed
                ),
                EnuTimelineItemData(
                    "Disetujui",
                    "16 Okt 2026, 09:00",
                    EnuTimelineNodeStatus.Completed
                ),
                EnuTimelineItemData("Menunggu Pengambilan", null, EnuTimelineNodeStatus.Current),
                EnuTimelineItemData("Batas Kembali", null, EnuTimelineNodeStatus.Upcoming)
            )

            DetailRiwayatState.BatasKembali -> listOf(
                EnuTimelineItemData(
                    "Diajukan",
                    "15 Okt 2026, 09:00",
                    EnuTimelineNodeStatus.Completed
                ),
                EnuTimelineItemData(
                    "Disetujui",
                    "16 Okt 2026, 09:00",
                    EnuTimelineNodeStatus.Completed
                ),
                EnuTimelineItemData(
                    "Diambil",
                    "16 Okt 2026, 10:00",
                    EnuTimelineNodeStatus.Completed
                ),
                EnuTimelineItemData(
                    "Batas Kembali",
                    "23 Okt 2026, 17:00",
                    EnuTimelineNodeStatus.Current
                )
            )

            DetailRiwayatState.Dikembalikan -> listOf(
                EnuTimelineItemData(
                    "Diajukan",
                    "15 Okt 2026, 09:00",
                    EnuTimelineNodeStatus.Completed
                ),
                EnuTimelineItemData(
                    "Disetujui",
                    "16 Okt 2026, 09:00",
                    EnuTimelineNodeStatus.Completed
                ),
                EnuTimelineItemData(
                    "Diambil",
                    "16 Okt 2026, 10:00",
                    EnuTimelineNodeStatus.Completed
                ),
                EnuTimelineItemData(
                    "Dikembalikan",
                    "21 Okt 2026, 14:00",
                    EnuTimelineNodeStatus.Completed
                )
            )
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            EnuTopBar(
                title = "Detail Riwayat",
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            EnuDetailHistoryCard(
                title = assetTitle,
                id = assetId
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = EnuTheme.colors.surfaceDefaultBase),
                border = BorderStroke(1.dp, EnuTheme.colors.borderDefaultMedium)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = "Aktivitas Pinjaman",
                        style = EnuTheme.typography.ui.labels.normalCase.large,
                        color = EnuTheme.colors.contentDefaultPrimary
                    )

                    EnuTimeline(items = timelineItems)
                }
            }

            when (state) {
                DetailRiwayatState.MenungguPengambilan -> {
                    EnuButton(
                        text = "Scan QR",
                        onClick = onScanQrClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                DetailRiwayatState.BatasKembali -> {
                    EnuButton(
                        text = "Kembalikan",
                        onClick = onKembalikanClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                else -> {}
            }
        }
    }
}

@Preview(showBackground = true, name = "Light")
@Composable
fun DetailRiwayatPagePreviewLight() {
    EnuTheme {
        DetailRiwayatPage(
            state = DetailRiwayatState.MenungguPengambilan,
            assetTitle = "Arduino Micro Controller",
            assetId = "HW-0019-A",
            currentRoute = "history",
            onBottomBarItemClick = {},
            onBackClick = {},
            onScanQrClick = {},
            onKembalikanClick = {}
        )
    }
}

@Preview(name = "Dark")
@Composable
fun DetailRiwayatPagePreviewDark() {
    EnuTheme(darkTheme = true) {
        DetailRiwayatPage(
            state = DetailRiwayatState.MenungguPersetujuan,
            assetTitle = "Arduino Micro Controller",
            assetId = "HW-0019-A",
            currentRoute = "history",
            onBottomBarItemClick = {},
            onBackClick = {},
            onScanQrClick = {},
            onKembalikanClick = {}
        )
    }
}