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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnuBorrowDialog(
    onDismissRequest: () -> Unit,
    onSubmitClick: (pesan: String, estimasi: String) -> Unit,
    modifier: Modifier = Modifier,
    isSubmitting: Boolean = false
) {
    var pesanInput by remember { mutableStateOf("") }
    var estimasiInput by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            selectableDates = object : SelectableDates {
                // Estimasi kembali gak masuk akal kalau di masa lalu
                override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                    utcTimeMillis >= startOfTodayUtcMillis()
            }
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            estimasiInput = formatEstimasiDate(millis)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

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
                    EnuTextField(
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
                    EnuTextField(
                        value = estimasiInput,
                        onValueChange = {},
                        placeholder = "Pilih tanggal",
                        readOnly = true,
                        onClick = { showDatePicker = true }
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

// DatePicker mengembalikan millis UTC di awal hari tanggal terpilih -- format eksplisit
// pakai timezone UTC di sini biar gak geser 1 hari tergantung timezone device.
private fun formatEstimasiDate(utcMillis: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return formatter.format(java.util.Date(utcMillis))
}

private fun startOfTodayUtcMillis(): Long {
    val calendar = java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        set(java.util.Calendar.HOUR_OF_DAY, 0)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}