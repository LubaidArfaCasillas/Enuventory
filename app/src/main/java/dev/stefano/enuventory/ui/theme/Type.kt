@file:Suppress("unused")
package dev.stefano.enuventory.ui.theme

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.stefano.enuventory.R

@OptIn(ExperimentalTextApi::class)
val GeistVariableFontFamily = FontFamily(
    Font(
        resId = R.font.geist_variable,
        variationSettings = FontVariation.Settings(FontVariation.Setting("wght", 400f))
    )
)

val EnuFontWeightRegular = FontWeight(400)
val EnuFontWeightSemiBold = FontWeight(600)
val EnuFontWeightBold = FontWeight(700)

val EnuFontSizeXs = 10.24.sp
val EnuFontSizeSm = 12.8.sp
val EnuFontSizeBase = 16.sp
val EnuFontSizeXl = 25.sp

val EnuLineHeightXs = 16.sp
val EnuLineHeightSm = 20.sp
val EnuLineHeightBase = 24.sp
val EnuLineHeightXl = 40.sp

class EnuTypography(
    val ui: UiStyles,
    val content: ContentStyles
)

class UiStyles(val labels: LabelStyles)

class LabelStyles(
    val normalCase: NormalCaseStyles,
    val allCaps: AllCapsStyles
)

class NormalCaseStyles(
    val base: TextStyle,
    val large: TextStyle,
    val small: TextStyle
)

class AllCapsStyles(
    val large: TextStyle,
    val base: TextStyle,
    val small: TextStyle
)

class ContentStyles(
    val headings: HeadingStyles,
    val body: BodyStyles
)

class HeadingStyles(
    val h3: TextStyle,
    val h6: TextStyle
)

class BodyStyles(
    val medium: TextStyle
)

val EnuTypographyStyle = EnuTypography(
    ui = UiStyles(
        labels = LabelStyles(
            normalCase = NormalCaseStyles(
                small = TextStyle(
                    fontFamily = GeistVariableFontFamily,
                    fontSize = EnuFontSizeXs,
                    lineHeight = EnuLineHeightXs,
                    fontWeight = EnuFontWeightSemiBold
                ),
                base = TextStyle(
                    fontFamily = GeistVariableFontFamily,
                    fontSize = EnuFontSizeSm,
                    lineHeight = EnuLineHeightSm,
                    fontWeight = EnuFontWeightSemiBold
                ),
                large = TextStyle(
                    fontFamily = GeistVariableFontFamily,
                    fontSize = EnuFontSizeBase,
                    lineHeight = EnuLineHeightBase,
                    fontWeight = EnuFontWeightSemiBold
                )
            ),
            allCaps = AllCapsStyles(
                small = TextStyle(
                    fontFamily = GeistVariableFontFamily,
                    fontSize = EnuFontSizeXs,
                    lineHeight = EnuLineHeightXs,
                    fontWeight = EnuFontWeightSemiBold
                ),
                base = TextStyle(
                    fontFamily = GeistVariableFontFamily,
                    fontSize = EnuFontSizeSm,
                    lineHeight = EnuLineHeightSm,
                    fontWeight = EnuFontWeightSemiBold
                ),
                large = TextStyle(
                    fontFamily = GeistVariableFontFamily,
                    fontSize = EnuFontSizeBase,
                    lineHeight = EnuLineHeightBase,
                    fontWeight = EnuFontWeightSemiBold
                )
            )
        )
    ),
    content = ContentStyles(
        headings = HeadingStyles(
            h3 = TextStyle(
                fontFamily = GeistVariableFontFamily,
                fontSize = EnuFontSizeXl,
                lineHeight = EnuLineHeightXl,
                fontWeight = EnuFontWeightSemiBold
            ),
            h6 = TextStyle(
                fontFamily = GeistVariableFontFamily,
                fontSize = EnuFontSizeSm,
                lineHeight = EnuLineHeightSm,
                fontWeight = EnuFontWeightSemiBold
            )
        ),
        body = BodyStyles(
            medium = TextStyle(
                fontFamily = GeistVariableFontFamily,
                fontSize = EnuFontSizeSm,
                lineHeight = EnuLineHeightBase,
                fontWeight = EnuFontWeightRegular
            )
        )
    )
)