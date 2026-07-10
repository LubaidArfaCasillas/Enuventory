package dev.stefano.enuventory.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

class EnuColors(
    val contentBrandPrimaryDefault: Color,
    val surfaceDefaultLevel3: Color,
    val contentDefaultPrimary: Color
)

val EnuLightColors = EnuColors(
    contentBrandPrimaryDefault = BaseBrandPrimary600,
    surfaceDefaultLevel3 = BaseNeutralGrey100,
    contentDefaultPrimary = BaseNeutralBlack100
)

val EnuDarkColors = EnuColors(
    contentBrandPrimaryDefault = BaseBrandPrimary600,
    surfaceDefaultLevel3 = BaseNeutralGrey700,
    contentDefaultPrimary = BaseNeutralWhite100
)

// provide color globally in the compose tree
val LocalEnuColors = staticCompositionLocalOf<EnuColors> {
    error("No EnuColors provided. Make sure to wrap your content in EnuTheme {}")
}

// provide typography globally in the compose tree
val LocalEnuTypography = staticCompositionLocalOf<EnuTypography> {
    error("No EnuTypography provided")
}

@Composable
fun EnuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) EnuDarkColors else EnuLightColors

    CompositionLocalProvider(
        LocalEnuColors provides colors,
        LocalEnuTypography provides EnuTypographyStyle,
        content = content
    )
}


object EnuTheme {
    val colors: EnuColors
        @Composable
        get() = LocalEnuColors.current

    val typography: EnuTypography
        @Composable
        get() = LocalEnuTypography.current
}