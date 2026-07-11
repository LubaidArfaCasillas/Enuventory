package dev.stefano.enuventory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.stefano.enuventory.ui.theme.EnuTheme

@Composable
fun EnuBorrowDialog(
    onDismissRequest: () -> Unit,
    onSubmitClick: (pesan: String, estimasi: String) -> Unit,
    modifier: Modifier = Modifier,
    isSubmitting: Boolean = false
) {
    var pesanInput by remember { mutableStateOf("") }
    var estimasiInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { if (!isSubmitting) onDismissRequest() }) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(EnuTheme.colors.surfaceDefaultBase)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row {
                        Text(
                            text = "Kirimkan pesan",
                            style = EnuTheme.typography.ui.labels.normalCase.small,
                            color = EnuTheme.colors.contentDefaultPrimary
                        )
                        Text(
                            text = " *",
                            style = EnuTheme.typography.ui.labels.normalCase.small,
                            color = EnuTheme.colors.contentSignalErrorDefault
                        )
                    }
                    EnuSearchField(
                        value = pesanInput,
                        onValueChange = { pesanInput = it },
                        placeholder = "Tuliskan keperluanmu disini..."
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row {
                        Text(
                            text = "Estimasi kembali",
                            style = EnuTheme.typography.ui.labels.normalCase.small,
                            color = EnuTheme.colors.contentDefaultPrimary
                        )
                        Text(
                            text = " *",
                            style = EnuTheme.typography.ui.labels.normalCase.small,
                            color = EnuTheme.colors.contentSignalErrorDefault
                        )
                    }
                    EnuSearchField(
                        value = estimasiInput,
                        onValueChange = { estimasiInput = it },
                        placeholder = "DD-MM-YY"
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                val buttonVariant =
                    if (isSubmitting) EnuButtonVariant.Loading else EnuButtonVariant.Normal

                EnuButton(
                    text = "Submit",
                    variant = buttonVariant,
                    onClick = { onSubmitClick(pesanInput, estimasiInput) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}