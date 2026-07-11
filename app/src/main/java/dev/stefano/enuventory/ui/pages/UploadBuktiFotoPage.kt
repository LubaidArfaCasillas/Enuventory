package dev.stefano.enuventory.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stefano.enuventory.R
import dev.stefano.enuventory.ui.components.EnuBottomBar
import dev.stefano.enuventory.ui.components.EnuBottomBarItemData
import dev.stefano.enuventory.ui.components.EnuButton
import dev.stefano.enuventory.ui.components.EnuButtonVariant
import dev.stefano.enuventory.ui.components.EnuTopBar
import dev.stefano.enuventory.ui.theme.EnuTheme

enum class UploadBuktiState {
    Capture, Error, PreviewImage, Submitting
}

@Composable
fun UploadBuktiFotoPage(
    state: UploadBuktiState,
    assetTitle: String,
    assetId: String,
    timestamp: String,
    currentRoute: String?,
    onBottomBarItemClick: (EnuBottomBarItemData) -> Unit,
    onBackClick: () -> Unit,
    onCaptureClick: () -> Unit,
    onUlangiClick: () -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EnuTopBar(
                title = "Upload Bukti Foto",
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (state == UploadBuktiState.Error) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_error),
                            contentDescription = null,
                            tint = EnuTheme.colors.contentSignalErrorDefault,
                            modifier = Modifier.size(56.dp)
                        )
                        Text(
                            text = "error log",
                            style = EnuTheme.typography.ui.labels.normalCase.small,
                            color = EnuTheme.colors.contentSignalErrorDefault
                        )
                    }
                }

                if (state == UploadBuktiState.PreviewImage || state == UploadBuktiState.Submitting) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = assetTitle,
                            style = EnuTheme.typography.ui.labels.normalCase.small,
                            color = Color.White,
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = "ID: $assetId",
                            style = EnuTheme.typography.ui.labels.normalCase.small,
                            color = Color.White,
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = timestamp,
                            style = EnuTheme.typography.ui.labels.normalCase.small,
                            color = Color.White,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }

            when (state) {
                UploadBuktiState.Capture -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(EnuTheme.colors.backgroundBrandPrimaryStrongDefault)
                                .clickable { onCaptureClick() }
                        )
                    }
                }

                UploadBuktiState.Error -> {
                    EnuButton(
                        text = "Ulangi",
                        onClick = onUlangiClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                UploadBuktiState.PreviewImage -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EnuButton(
                            text = "Submit",
                            onClick = onSubmitClick,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Ulangi",
                            style = EnuTheme.typography.ui.labels.normalCase.large,
                            color = EnuTheme.colors.contentDefaultPrimary,
                            modifier = Modifier.clickable { onUlangiClick() }
                        )
                    }
                }

                UploadBuktiState.Submitting -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EnuButton(
                            text = "Submit",
                            variant = EnuButtonVariant.Loading,
                            onClick = {},
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Ulangi",
                            style = EnuTheme.typography.ui.labels.normalCase.large,
                            color = EnuTheme.colors.contentDefaultDisabled
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Light")
@Composable
fun UploadBuktiFotoPagePreviewLight() {
    EnuTheme {
        UploadBuktiFotoPage(
            state = UploadBuktiState.PreviewImage,
            assetTitle = "Arduino Micro Controller",
            assetId = "HW-0019-A",
            timestamp = "4 Jul 2026, 15:00",
            currentRoute = "history",
            onBottomBarItemClick = {},
            onBackClick = {},
            onCaptureClick = {},
            onUlangiClick = {},
            onSubmitClick = {}
        )
    }
}

@Preview(name = "Dark")
@Composable
fun UploadBuktiFotoPagePreviewDark() {
    EnuTheme(darkTheme = true) {
        UploadBuktiFotoPage(
            state = UploadBuktiState.PreviewImage,
            assetTitle = "Arduino Micro Controller",
            assetId = "HW-0019-A",
            timestamp = "4 Jul 2026, 15:00",
            currentRoute = "history",
            onBottomBarItemClick = {},
            onBackClick = {},
            onCaptureClick = {},
            onUlangiClick = {},
            onSubmitClick = {}
        )
    }
}