package com.example.lifeplan.main_view

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeplan.R
import com.example.lifeplan.custom.dialog.FrequencyDialog
import com.example.lifeplan.custom.dialog.ShowPickDateDialog
import com.example.lifeplan.custom.dialog.ShowPickDateRangeDialog
import com.example.lifeplan.custom.dialog.ShowPickMultipleDateDialog
import com.example.lifeplan.custom.dialog.ShowPickTimeDialog
import com.example.lifeplan.custom.item.FrequencyItems
import com.example.lifeplan.custom.item.ItemSchedule
import com.example.lifeplan.schedule_dao.Schedule
import com.example.lifeplan.viewModel.ScheduleViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel,
    context: Context
) {
    val itemSchedule by viewModel.allSchedule.observeAsState(emptyList())
    var isShowAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        HeaderScreen(
            title = stringResource(R.string.schedule),
            icon = Icons.Default.AlarmAdd
        ) {
            // Click để thêm lịch trình
            isShowAddDialog = !isShowAddDialog
        }

        if (isShowAddDialog) {
            // hiện dialog thêm lịch trình
            AddSchedule(
                modifier = Modifier
                    .padding(8.dp)
                    .scrollable(
                        enabled = true,
                        state = rememberScrollState(),
                        orientation = Orientation.Vertical
                    ),
                onSave = { schedule ->
                    viewModel.addSchedule(schedule)
                    isShowAddDialog = !isShowAddDialog
                },
                onDismiss = { isShowAddDialog = !isShowAddDialog },
                context = context
            )
        }

        Spacer(modifier = Modifier.size(16.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .scrollable(
                    enabled = true,
                    state = rememberScrollState(),
                    orientation = Orientation.Vertical
                ),
            contentPadding = PaddingValues(top = 16.dp)
        ) {
            items(itemSchedule, key = { it.id }) { schedule ->
                // Truyền đúng thuộc tính của đối tượng Schedule
                ItemSchedule(
                    modifier = Modifier,
                    schedule = schedule,
                    onDelete = {
                        viewModel.deleteSchedule(schedule) // Xóa lịch trình
                    },
                    onUpdate = {
                        viewModel.updateSchedule(it) // Cập nhật lịch trình
                    },
                    context = context
                )
            }
        }
    }
}

@Composable
fun HeaderScreen(
    title: String,
    icon: ImageVector,
    onClickAdd: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier,
            textAlign = TextAlign.Center,
            text = title,
            maxLines = 1,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp)
        )

        IconButton(
            onClick = onClickAdd,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Click to Add",
                Modifier.size(50.dp)
            )
        }
    }
}

@Composable
fun AddSchedule(
    modifier: Modifier,
    onDismiss: () -> Unit,
    onSave: (Schedule) -> Unit,
    context: Context
) {
    val calendar = Calendar.getInstance()

    val timeNow = SimpleDateFormat("hh:mm", Locale.getDefault()).format(calendar.time)
    val dateNow = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)

    var note by rememberSaveable { mutableStateOf("") } // ghi chú
    var time by rememberSaveable { mutableStateOf(timeNow) } // thời gian giờ:phút
    var freq by rememberSaveable { mutableStateOf(FrequencyItems.ONCE) } // tần suất
    var startDate by rememberSaveable { mutableStateOf(dateNow) } // ngày bắt đầu
    var endDate by rememberSaveable { mutableStateOf(dateNow) } // ngày kết thúc
    var numDays by rememberSaveable { mutableIntStateOf(0) } // số ngày chọn của danh sách ngày lẻ
    var selectedDates by rememberSaveable { mutableStateOf<List<String>>(emptyList()) } // danh sách ngày lẻ

    var isShowTimeDialog by remember { mutableStateOf(false) } // có mở dialog thời gian k
    var isShowFreqDialog by remember { mutableStateOf(false) } // có mở dialog tần suất k
    var showDate by remember { mutableIntStateOf(0) } // kiểu tần suất ( 0,1,2,3)
    var isShowDateDialog by remember { mutableStateOf(false) } // có mở dialog ngày k

    AlertDialog(
        onDismissRequest = { onDismiss() },
        text = {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .scrollable(
                        enabled = true,
                        state = rememberScrollState(),
                        orientation = Orientation.Vertical
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                OutlinedTextField(
                    modifier = modifier.fillMaxWidth(),
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(stringResource(R.string.note)) },
                    placeholder = { Text(stringResource(R.string.write_note_here)) },
                )

                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = modifier,
                        text = stringResource(R.string.time)
                    )
                    Text(
                        modifier = modifier.clickable {
                            isShowTimeDialog = !isShowTimeDialog
                        },
                        text = time
                    )
                }

                if (isShowTimeDialog) {
                    ShowPickTimeDialog(
                        time = time,
                        onSave = { newTime -> time = newTime },
                        onDismiss = { isShowTimeDialog = !isShowTimeDialog }
                    )
                }

                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = modifier,
                        text = stringResource(R.string.repeat)
                    )
                    Spacer(modifier = modifier.width(8.dp))
                    Text(
                        modifier = modifier.clickable { isShowFreqDialog = !isShowFreqDialog },
                        text = freq.getDescription(context)
                    )
                    if (isShowFreqDialog) {
                        FrequencyDialog(
                            modifier = modifier,
                            frequency = freq,
                            onFrequencyItems = { newFrequency ->
                                freq = newFrequency
                            },
                            onSaveDateStart = {
                                startDate = it
                            },
                            onSaveDateEnd = {
                                endDate = it
                            },
                            onDateSelected = {
                                selectedDates = it
                                numDays = it.size
                            },
                            onDismiss = { isShowFreqDialog = !isShowFreqDialog },
                            context = context
                        )
                    }
                }

                showDate = when (freq) {
                    FrequencyItems.ONCE -> {
                        0
                    }

                    FrequencyItems.DAILY, FrequencyItems.WEEKLY, FrequencyItems.MONTHLY, FrequencyItems.YEARLY -> {
                        1
                    }

                    FrequencyItems.DATETODATE -> {
                        2
                    }

                    FrequencyItems.PICKDATE -> {
                        3
                    }
                }

                when (showDate) {
                    0 -> {
                        // chỉ cần hiện ngày thực hiện 1 lần
                        Row(
                            modifier = modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = modifier,
                                text = stringResource(R.string.implementation_date)
                            )
                            Text(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .clickable { isShowDateDialog = !isShowDateDialog },
                                text = startDate
                            )
                        }
                    }

                    1 -> {
                        // chỉ cần hiện ngày bắt đầu sau đó lặp theo thời gian đã định trước
                        Row(
                            modifier = modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = modifier,
                                text = stringResource(R.string.start_date)
                            )
                            Text(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .clickable { isShowDateDialog = !isShowDateDialog },
                                text = startDate
                            )
                        }
                    }

                    2 -> {
                        // hiện ngày bắt đầu và ngày kết thúc
                        Text(
                            modifier = modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth()
                                .clickable { isShowDateDialog = !isShowDateDialog },
                            text = stringResource(R.string.from_to, startDate, endDate)
                        )
                    }

                    3 -> {
                        // hiện danh sách ngày đã chọn (có bao gồm số ngày)
                        Row(
                            modifier = modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = modifier,
                                text = stringResource(R.string.selected_date)
                            )
                            Text(
                                modifier = modifier.clickable {
                                    isShowDateDialog = !isShowDateDialog
                                },
                                text = numDays.toString()
                            )
                        }
                    }
                }

                if (isShowDateDialog) {
                    when (showDate) {
                        0 -> ShowPickDateDialog(
                            date = startDate,
                            onSave = { newDate -> startDate = newDate },
                            onDismiss = { isShowDateDialog = !isShowDateDialog }
                        )

                        1 -> ShowPickDateDialog(
                            date = startDate,
                            onSave = { newDate -> startDate = newDate },
                            onDismiss = { isShowDateDialog = !isShowDateDialog }
                        )

                        2 -> ShowPickDateRangeDialog(
                            modifier = modifier,
                            onSave = { s, e ->
                                startDate = s
                                endDate = e
                            },
                            onDismiss = { isShowDateDialog = !isShowDateDialog },
                            context = context
                        )

                        3 -> ShowPickMultipleDateDialog(
                            modifier = modifier,
                            nDays = numDays.toString(),
                            onSave = { dates ->
                                selectedDates = dates
                            },
                            onDismiss = { isShowDateDialog = !isShowDateDialog }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                modifier = modifier,
                onClick = {
                    val newSchedule =
                        Schedule(
                            note = note,
                            time = time,
                            frequency = freq,
                            dateStart = if (showDate == 0 || showDate == 1 || showDate == 2) startDate else "",
                            dateEnd = if (showDate == 2) endDate else "",
                            pickedDate = if (showDate == 3) selectedDates else emptyList(),
                            isEnabled = true
                        )
                    onSave(newSchedule)
                }) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            Button(
                modifier = modifier,
                onClick = { onDismiss() }) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}