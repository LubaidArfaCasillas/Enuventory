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
val EnuFontWeightSemiBold = FontWeight(600)
val EnuFontSizeSm = 12.8.sp
val EnuLineHeightSm = 20.sp

class EnuTypography(
    val ui: UiStyles,
    val content: ContentStyles
)
class UiStyles(val labels: LabelStyles)
class LabelStyles(val normalCase: NormalCaseStyles)
class NormalCaseStyles(val base: TextStyle)
class ContentStyles(val headings: HeadingStyles)
class HeadingStyles(val h3: TextStyle)

val EnuTypographyStyle = EnuTypography(
    ui = UiStyles(
        labels = LabelStyles(
            normalCase = NormalCaseStyles(
                base = TextStyle(
                    fontFamily = GeistVariableFontFamily,
                    fontSize = EnuFontSizeSm,
                    lineHeight = EnuLineHeightSm,
                    fontWeight = EnuFontWeightSemiBold
                )
            )
        )
    ),
    content = ContentStyles(
        headings = HeadingStyles(
            h3 = TextStyle(
                fontFamily = GeistVariableFontFamily,
                fontSize = 20.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold
            )
        )
    )
)