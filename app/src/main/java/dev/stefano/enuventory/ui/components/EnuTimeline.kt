package dev.stefano.enuventory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stefano.enuventory.ui.theme.EnuTheme

data class EnuTimelineItemData(
    val title: String,
    val date: String?,
    val status: EnuTimelineNodeStatus
)

@Composable
fun EnuTimeline(
    items: List<EnuTimelineItemData>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(32.dp)
                ) {
                    EnuTimelineNodeIndicator(status = item.status)

                    if (index < items.lastIndex) {
                        val lineColor =
                            if (items[index + 1].status == EnuTimelineNodeStatus.Upcoming) {
                                EnuTheme.colors.backgroundDisabled
                            } else {
                                EnuTheme.colors.backgroundBrandPrimaryStrongDefault
                            }

                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(44.dp)
                                .background(lineColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                val titleColor = when (item.status) {
                    EnuTimelineNodeStatus.Current -> EnuTheme.colors.contentBrandPrimaryDefault
                    EnuTimelineNodeStatus.Upcoming -> EnuTheme.colors.contentDefaultDisabled
                    else -> EnuTheme.colors.contentDefaultPrimary
                }

                Column(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = item.title,
                        style = EnuTheme.typography.ui.labels.normalCase.large,
                        color = titleColor
                    )

                    if (!item.date.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))

                        val dateColor = if (item.status == EnuTimelineNodeStatus.Current) {
                            EnuTheme.colors.contentBrandPrimaryDefault
                        } else {
                            EnuTheme.colors.contentDefaultSubtle
                        }

                        Text(
                            text = item.date,
                            style = EnuTheme.typography.content.headings.h6,
                            color = dateColor
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Light")
@Composable
fun EnuTimelinePreviewLight() {
    EnuTheme {
        val dummyTimeline = listOf(
            EnuTimelineItemData("Diajukan", "15 Okt 2026, 09:00", EnuTimelineNodeStatus.Completed),
            EnuTimelineItemData("Disetujui", "16 Okt 2026, 09:00", EnuTimelineNodeStatus.Completed),
            EnuTimelineItemData("Waiting Approval", null, EnuTimelineNodeStatus.Current),
            EnuTimelineItemData("Selesai", null, EnuTimelineNodeStatus.Upcoming)
        )
        Box(modifier = Modifier.padding(24.dp)) {
            EnuTimeline(items = dummyTimeline)
        }
    }
}

@Preview(name = "Dark")
@Composable
fun EnuTimelinePreviewDark() {
    EnuTheme(darkTheme = true) {
        val dummyTimeline = listOf(
            EnuTimelineItemData("Diajukan", "15 Okt 2026, 09:00", EnuTimelineNodeStatus.Completed),
            EnuTimelineItemData("Disetujui", "16 Okt 2026, 09:00", EnuTimelineNodeStatus.Completed),
            EnuTimelineItemData("Waiting Approval", null, EnuTimelineNodeStatus.Current),
            EnuTimelineItemData("Selesai", null, EnuTimelineNodeStatus.Upcoming)
        )
        Box(modifier = Modifier.padding(24.dp)) {
            EnuTimeline(items = dummyTimeline)
        }
    }
}