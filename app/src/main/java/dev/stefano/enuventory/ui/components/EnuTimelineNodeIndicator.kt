package dev.stefano.enuventory.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stefano.enuventory.R
import dev.stefano.enuventory.ui.theme.EnuTheme

enum class EnuTimelineNodeStatus {
    Completed, Current, Rejected, Upcoming
}

@Composable
fun EnuTimelineNodeIndicator(
    status: EnuTimelineNodeStatus,
    modifier: Modifier = Modifier
) {
    val size = 32.dp

    when (status) {
        EnuTimelineNodeStatus.Completed -> {
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(EnuTheme.colors.backgroundBrandPrimaryStrongDefault),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = null,
                    tint = EnuTheme.colors.contentBrandPrimaryOnStrong,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        EnuTimelineNodeStatus.Current -> {
            Box(
                modifier = modifier
                    .size(size)
                    .border(
                        BorderStroke(2.dp, EnuTheme.colors.backgroundBrandPrimaryStrongDefault),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(EnuTheme.colors.backgroundBrandPrimaryStrongDefault)
                )
            }
        }

        EnuTimelineNodeStatus.Rejected -> {
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(EnuTheme.colors.contentSignalErrorDefault),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = null,
                    tint = EnuTheme.colors.contentBrandPrimaryOnStrong,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        EnuTimelineNodeStatus.Upcoming -> {
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(EnuTheme.colors.backgroundDisabled)
            )
        }
    }
}

@Preview(showBackground = true, name = "Light")
@Composable
fun EnuTimelineNodeIndicatorPreviewLight() {
    EnuTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EnuTimelineNodeIndicator(status = EnuTimelineNodeStatus.Completed)
            EnuTimelineNodeIndicator(status = EnuTimelineNodeStatus.Current)
            EnuTimelineNodeIndicator(status = EnuTimelineNodeStatus.Rejected)
            EnuTimelineNodeIndicator(status = EnuTimelineNodeStatus.Upcoming)
        }
    }
}

@Preview(name = "Dark")
@Composable
fun EnuTimelineNodeIndicatorPreviewDark() {
    EnuTheme(darkTheme = true) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EnuTimelineNodeIndicator(status = EnuTimelineNodeStatus.Completed)
            EnuTimelineNodeIndicator(status = EnuTimelineNodeStatus.Current)
            EnuTimelineNodeIndicator(status = EnuTimelineNodeStatus.Rejected)
            EnuTimelineNodeIndicator(status = EnuTimelineNodeStatus.Upcoming)
        }
    }
}