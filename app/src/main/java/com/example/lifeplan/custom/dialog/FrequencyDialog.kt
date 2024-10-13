package com.example.lifeplan.custom.dialog

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.lifeplan.R
import com.example.lifeplan.custom.item.FrequencyItems

@Composable
fun FrequencyDialog(
    modifier: Modifier,
    frequency: FrequencyItems,
    onFrequencyItems: (FrequencyItems) -> Unit,
    onSaveDateStart: (String) -> Unit,
    onSaveDateEnd: (String) -> Unit,
    onDateSelected: (List<String>) -> Unit,
    onDismiss: () -> Unit,
    context: Context
) {
    var freq by remember { mutableStateOf(frequency) }
    var isShowDatePicker by remember { mutableStateOf(false) }
    var selectedDates by remember { mutableStateOf<List<String>>(emptyList()) }

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(stringResource(R.string.select_frequency_for_event)) },
        text = {
            Column(
                modifier = modifier
                    .fillMaxWidth()
            ) {
                FrequencyItems.entries
                    .forEach { frequencyItems ->
                        Row(
                            modifier = modifier,
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            RadioButton(
                                selected = freq == frequencyItems,
                                onClick = {
                                    freq = frequencyItems
                                    isShowDatePicker = !isShowDatePicker
                                }
                            )
                            Text(
                                modifier = modifier
                                    .fillMaxWidth(),
                                text = frequencyItems.getDescription(context)
                            )
                        }
                    }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onFrequencyItems(freq)
                when (freq) {
                    FrequencyItems.ONCE, FrequencyItems.DAILY, FrequencyItems.WEEKLY, FrequencyItems.MONTHLY, FrequencyItems.YEARLY
                    -> onSaveDateStart(startDate)

                    FrequencyItems.DATETODATE -> {
                        onSaveDateStart(startDate)
                        onSaveDateEnd(endDate)
                    }

                    FrequencyItems.PICKDATE -> onDateSelected(selectedDates)
                }

                onDismiss()
            }) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    //click để hủy
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
    if (isShowDatePicker) {
        when (freq) {
            FrequencyItems.ONCE, FrequencyItems.DAILY, FrequencyItems.WEEKLY, FrequencyItems.MONTHLY, FrequencyItems.YEARLY -> {
                ShowPickDateDialog(
                    date = startDate,
                    onSave = { d ->
                        startDate = d
                    },
                    onDismiss = { isShowDatePicker = !isShowDatePicker })
            }

            FrequencyItems.DATETODATE -> {
                // Chọn khoảng thời gian từ ngày đến ngày
                ShowPickDateRangeDialog(
                    modifier = modifier,
                    onSave = { s, e ->
                        startDate = s
                        endDate = e
                    },
                    onDismiss = { isShowDatePicker = !isShowDatePicker },
                    context = context
                )
            }

            FrequencyItems.PICKDATE -> {
                // Chọn nhiều ngày riêng lẻ
                ShowPickMultipleDateDialog(
                    modifier = modifier,
                    nDays = selectedDates.size.toString(),
                    onSave = { dates ->
                        selectedDates = dates
                    },
                    onDismiss = { isShowDatePicker = !isShowDatePicker })
            }
        }
    }
}