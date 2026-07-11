@file:Suppress("unused")
package dev.stefano.enuventory.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

class EnuColors(
    val contentBrandPrimaryDefault: Color,
    val surfaceDefaultLevel3: Color,
    val contentDefaultPrimary: Color,
    val contentDefaultInversePrimary: Color,
    val backgroundBrandPrimaryStrongDefault: Color,
    val backgroundDisabled: Color,
    val backgroundPrimaryStrongPressed: Color,
    val backgroundSignalWarningStrongDefault: Color,
    val contentSignalErrorDefault: Color,
    val backgroundSignalErrorMediumDefault: Color,
    val contentSignalSuccessDefault: Color,
    val backgroundSignalSuccessMediumDefault: Color,
    val contentBrandPrimaryOnStrong: Color,
    val contentDefaultDisabled: Color,
    val contentSignalSuccessOnSubtle: Color,
    val contentSignalErrorOnSubtle: Color,
    val contentSignalWarningOnSubtle: Color,
    val backgroundSignalWarningMediumDefault: Color
)

val EnuLightColors = EnuColors(
    contentBrandPrimaryDefault = BaseBrandPrimary600,
    contentBrandPrimaryOnStrong = BaseNeutralWhite100,
    contentDefaultPrimary = BaseNeutralBlack100,
    contentDefaultInversePrimary = BaseNeutralWhite100,
    surfaceDefaultLevel3 = BaseNeutralGrey100,

    backgroundBrandPrimaryStrongDefault = BaseBrandPrimary500,
    backgroundDisabled = BaseNeutralBlack10,
    backgroundPrimaryStrongPressed = BaseBrandPrimary700,
    backgroundSignalWarningStrongDefault = BaseSignalWarning500,
    contentSignalErrorDefault = BaseSignalError600,
    backgroundSignalErrorMediumDefault = BaseSignalError200,
    contentSignalSuccessDefault = BaseSignalSuccess600,
    backgroundSignalSuccessMediumDefault = BaseSignalSuccess200,
    contentDefaultDisabled = BaseNeutralGrey400,

    contentSignalSuccessOnSubtle = BaseSignalSuccess800,
    contentSignalErrorOnSubtle = BaseSignalError800,
    contentSignalWarningOnSubtle = BaseSignalWarning800,
    backgroundSignalWarningMediumDefault = BaseSignalWarning200
)

val EnuDarkColors = EnuColors(
    contentBrandPrimaryDefault = BaseBrandPrimary600,
    contentDefaultPrimary = BaseNeutralWhite100,
    contentDefaultInversePrimary = BaseNeutralBlack100,
    contentBrandPrimaryOnStrong = BaseNeutralWhite100,
    surfaceDefaultLevel3 = BaseNeutralGrey700,

    backgroundBrandPrimaryStrongDefault = BaseBrandPrimary500,
    backgroundDisabled = BaseNeutralWhite10,
    backgroundPrimaryStrongPressed = BaseBrandPrimary300,
    backgroundSignalWarningStrongDefault = BaseSignalWarning500,
    contentSignalErrorDefault = BaseSignalError600,
    backgroundSignalErrorMediumDefault = BaseSignalError700,
    contentSignalSuccessDefault = BaseSignalSuccess600,
    backgroundSignalSuccessMediumDefault = BaseSignalSuccess700,
    contentDefaultDisabled = BaseNeutralGrey600,

    contentSignalSuccessOnSubtle = BaseSignalSuccess400,
    contentSignalErrorOnSubtle = BaseSignalError400,
    contentSignalWarningOnSubtle = BaseSignalWarning400,
    backgroundSignalWarningMediumDefault = BaseSignalWarning700
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