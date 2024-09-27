package com.example.lifeplan.custom_dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.unit.dp
import com.example.lifeplan.custom_item.FrequencyItems

@Composable
fun FrequencyDialog(
    modifier: Modifier = Modifier,
    frequency: FrequencyItems,
    onFrequencyItems: (FrequencyItems) -> Unit,
    onSaveDateStart: (String) -> Unit,
    onSaveDateEnd: (String) -> Unit,
    onDateSelected: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var freq by remember { mutableStateOf(frequency) }
    var isShowDatePicker by remember { mutableStateOf(false) }
    var selectedDates by remember { mutableStateOf<List<String>>(emptyList()) }
    var startDate by remember { mutableStateOf("Click để chọn ngày") }
    var endDate by remember { mutableStateOf("Click để chọn ngày") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
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
                Text("Lưu")
            }
        },
        title = { Text("Chọn tần suất cho sự kiện") },
        text = {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                FrequencyItems.entries
                    .forEach { frequencyItems ->
                        Row(
                            modifier = modifier,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = freq == frequencyItems,
                                onClick = {
                                    freq = frequencyItems
                                    isShowDatePicker = true
                                }
                            )
                            Text(
                                modifier = modifier
                                    .fillMaxWidth(1f)
                                    .padding(start = 8.dp),
                                text = frequencyItems.desc
                            )
                        }
                    }
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
                        isShowDatePicker = false
                    },
                    onDismiss = { isShowDatePicker = false })
            }

            FrequencyItems.DATETODATE -> {
                // Chọn khoảng thời gian từ ngày đến ngày
                ShowPickDateRangeDialog(
                    modifier = modifier,
                    onSave = { s, e ->
                        startDate = s
                        endDate = e
                        isShowDatePicker = false
                    }, onDismiss = { isShowDatePicker = false })
            }

            FrequencyItems.PICKDATE -> {
                // Chọn nhiều ngày riêng lẻ
                ShowPickMultipleDateDialog(
                    modifier = modifier,
                    nDays = selectedDates.size.toString(),
                    onSave = { dates ->
                        selectedDates = dates
                        isShowDatePicker = false
                    },
                    onDismiss = { isShowDatePicker = false })
            }
        }
    }
}
