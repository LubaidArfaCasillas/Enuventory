package dev.stefano.enuventory.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stefano.enuventory.ui.theme.EnuTheme

enum class EnuButtonVariant {
    Normal, Disabled, Pressed, Loading, Warning, Danger, Success
}

@Composable
fun EnuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: EnuButtonVariant = EnuButtonVariant.Normal
) {
    val backgroundColor: Color
    val contentColor: Color

    when (variant) {
        EnuButtonVariant.Normal -> {
            backgroundColor = EnuTheme.colors.backgroundBrandPrimaryStrongDefault
            contentColor = EnuTheme.colors.contentBrandPrimaryOnStrong
        }

        EnuButtonVariant.Disabled -> {
            backgroundColor = EnuTheme.colors.backgroundDisabled
            contentColor = EnuTheme.colors.contentDefaultDisabled
        }

        EnuButtonVariant.Pressed, EnuButtonVariant.Loading -> {
            backgroundColor = EnuTheme.colors.backgroundPrimaryStrongPressed
            contentColor = EnuTheme.colors.contentBrandPrimaryOnStrong
        }

        EnuButtonVariant.Warning -> {
            backgroundColor = EnuTheme.colors.backgroundSignalWarningStrongDefault
            contentColor = EnuTheme.colors.contentBrandPrimaryOnStrong
        }

        EnuButtonVariant.Danger -> {
            backgroundColor = EnuTheme.colors.backgroundSignalErrorMediumDefault
            contentColor = EnuTheme.colors.contentSignalErrorDefault
        }

        EnuButtonVariant.Success -> {
            backgroundColor = EnuTheme.colors.backgroundSignalSuccessMediumDefault
            contentColor = EnuTheme.colors.contentSignalSuccessDefault
        }
    }

    val isEnabled = variant != EnuButtonVariant.Disabled && variant != EnuButtonVariant.Loading

    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            disabledContainerColor = backgroundColor,
            disabledContentColor = contentColor
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (variant == EnuButtonVariant.Loading) {
                CircularProgressIndicator(
                    color = contentColor,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = text,
                    style = EnuTheme.typography.ui.labels.normalCase.large,
                    color = contentColor
                )
            }
        }
    }
}

@Preview(name = "Light")
@Composable
fun EnuButtonPreviewLight() {
    EnuTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EnuButton(text = "Button Text", variant = EnuButtonVariant.Normal, onClick = {})
            EnuButton(text = "Button Text", variant = EnuButtonVariant.Disabled, onClick = {})
            EnuButton(text = "Button Text", variant = EnuButtonVariant.Pressed, onClick = {})
            EnuButton(text = "Button Text", variant = EnuButtonVariant.Loading, onClick = {})
            EnuButton(text = "Button Text", variant = EnuButtonVariant.Warning, onClick = {})
            EnuButton(text = "Button Text", variant = EnuButtonVariant.Danger, onClick = {})
            EnuButton(text = "Button Text", variant = EnuButtonVariant.Success, onClick = {})
        }
    }
}

@Preview(name = "Dark")
@Composable
fun EnuButtonPreviewDark() {
    EnuTheme(darkTheme = true) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EnuButton(text = "Button Text", variant = EnuButtonVariant.Normal, onClick = {})
            EnuButton(text = "Button Text", variant = EnuButtonVariant.Disabled, onClick = {})
            EnuButton(text = "Button Text", variant = EnuButtonVariant.Pressed, onClick = {})
            EnuButton(text = "Button Text", variant = EnuButtonVariant.Loading, onClick = {})
            EnuButton(text = "Button Text", variant = EnuButtonVariant.Warning, onClick = {})
            EnuButton(text = "Button Text", variant = EnuButtonVariant.Danger, onClick = {})
            EnuButton(text = "Button Text", variant = EnuButtonVariant.Success, onClick = {})
        }
    }
}