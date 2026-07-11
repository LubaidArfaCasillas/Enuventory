package dev.stefano.enuventory.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stefano.enuventory.R
import dev.stefano.enuventory.ui.theme.EnuTheme

@Composable
fun EnuSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = {
        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = "Search Icon"
        )
    }
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        textStyle = EnuTheme.typography.ui.labels.normalCase.base,
        shape = RoundedCornerShape(12.dp),
        placeholder = {
            Text(
                text = placeholder,
                style = EnuTheme.typography.ui.labels.normalCase.base,
                color = EnuTheme.colors.contentDefaultSubtle
            )
        },
        leadingIcon = leadingIcon,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = EnuTheme.colors.borderDefaultMedium,
            focusedBorderColor = EnuTheme.colors.contentDefaultPrimary,

            focusedTextColor = EnuTheme.colors.contentDefaultPrimary,
            unfocusedTextColor = EnuTheme.colors.contentDefaultPrimary,

            focusedLeadingIconColor = EnuTheme.colors.contentDefaultPrimary,
            unfocusedLeadingIconColor = EnuTheme.colors.contentDefaultSubtle
        )
    )
}

@Preview(showBackground = true, name = "Light")
@Composable
fun EnuSearchFieldPreviewLight() {
    var searchValue1 by remember { mutableStateOf("") }
    var searchValue2 by remember { mutableStateOf("") }

    EnuTheme(darkTheme = false) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            EnuSearchField(
                value = searchValue1,
                onValueChange = { searchValue1 = it },
                placeholder = "Search Placeholder"
            )


            EnuSearchField(
                value = searchValue2,
                onValueChange = { searchValue2 = it },
                placeholder = "Search Placeholder"
            )
        }
    }
}

@Preview(name = "Dark")
@Composable
fun EnuSearchFieldPreviewDark() {
    var searchValue1 by remember { mutableStateOf("") }
    var searchValue2 by remember { mutableStateOf("") }

    EnuTheme(darkTheme = true) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            EnuSearchField(
                value = searchValue1,
                onValueChange = { searchValue1 = it },
                placeholder = "Search Placeholder"
            )


            EnuSearchField(
                value = searchValue2,
                onValueChange = { searchValue2 = it },
                placeholder = "Search Placeholder"
            )
        }
    }
}