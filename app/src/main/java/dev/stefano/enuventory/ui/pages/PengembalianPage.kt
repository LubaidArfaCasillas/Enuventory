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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stefano.enuventory.ui.components.EnuBottomBar
import dev.stefano.enuventory.ui.components.EnuBottomBarItemData
import dev.stefano.enuventory.ui.components.EnuButton
import dev.stefano.enuventory.ui.components.EnuDetailHistoryCard
import dev.stefano.enuventory.ui.components.EnuTopBar
import dev.stefano.enuventory.ui.theme.EnuTheme

@Composable
fun PengembalianPage(
    assetTitle: String,
    assetId: String,
    currentRoute: String?,
    onBottomBarItemClick: (EnuBottomBarItemData) -> Unit,
    onBackClick: () -> Unit,
    onUploadBuktiClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EnuTopBar(
                title = "Pengembalian",
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Panduan Pengembalian",
                        style = EnuTheme.typography.ui.labels.normalCase.large,
                        color = EnuTheme.colors.contentDefaultPrimary
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "1. Datang ke Kantor Enuma",
                            style = EnuTheme.typography.content.body.medium,
                            color = EnuTheme.colors.contentDefaultPrimary
                        )
                        Text(
                            text = "2. Kembalikan barang ke tempat yang disediakan",
                            style = EnuTheme.typography.content.body.medium,
                            color = EnuTheme.colors.contentDefaultPrimary
                        )
                        Text(
                            text = "3. Upload bukti foto",
                            style = EnuTheme.typography.content.body.medium,
                            color = EnuTheme.colors.contentDefaultPrimary
                        )
                    }
                }
            }

            EnuButton(
                text = "Upload Bukti Foto",
                onClick = onUploadBuktiClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, name = "Light")
@Composable
fun PengembalianPagePreviewLight() {
    EnuTheme {
        PengembalianPage(
            assetTitle = "Arduino Micro Controller",
            assetId = "HW-0019-A",
            currentRoute = "history",
            onBottomBarItemClick = {},
            onBackClick = {},
            onUploadBuktiClick = {}
        )
    }
}

@Preview(name = "Dark")
@Composable
fun PengembalianPagePreviewDark() {
    EnuTheme(darkTheme = true) {
        PengembalianPage(
            assetTitle = "Arduino Micro Controller",
            assetId = "HW-0019-A",
            currentRoute = "history",
            onBottomBarItemClick = {},
            onBackClick = {},
            onUploadBuktiClick = {}
        )
    }
}