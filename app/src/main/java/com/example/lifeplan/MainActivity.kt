package com.example.lifeplan

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lifeplan.custom_dialog.FrequencyDialog
import com.example.lifeplan.custom_dialog.ShowPickDateDialog
import com.example.lifeplan.custom_dialog.ShowPickDateRangeDialog
import com.example.lifeplan.custom_dialog.ShowPickMultipleDateDialog
import com.example.lifeplan.custom_dialog.ShowPickTimeDialog
import com.example.lifeplan.custom_item.FrequencyItems
import com.example.lifeplan.custom_item.ItemSchedule
import com.example.lifeplan.dao.Schedule
import com.example.lifeplan.ui.theme.LifePlanTheme
import com.example.lifeplan.viewModel.ScheduleViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifePlanTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: ScheduleViewModel = viewModel(
                        factory = ViewModelProvider
                            .AndroidViewModelFactory(
                                LocalContext.current.applicationContext
                                        as android.app.Application
                            )
                    )
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(modifier: Modifier = Modifier, viewModel: ScheduleViewModel) {
    val itemSchedule by viewModel.allSchedule.observeAsState(emptyList())
    var isShowAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        HeaderScreen(modifier, title = "Lịch trình") {
            // Click để thêm lịch trình
            isShowAddDialog = !isShowAddDialog
        }

        if (isShowAddDialog) {
            // hiện dialog thêm lịch trình
            AddSchedule(
                modifier = modifier
                    .padding(16.dp),
                onSave = { schedule ->
                    viewModel.addSchedule(schedule)
                    isShowAddDialog = !isShowAddDialog
                },
                onDismiss = { isShowAddDialog = !isShowAddDialog }
            )
        }

        Spacer(modifier = modifier.size(16.dp))
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(itemSchedule, key = { it.id }) { schedule ->
                // Truyền đúng thuộc tính của đối tượng Schedule
                ItemSchedule(
                    modifier = modifier,
                    schedule = schedule,
                    onDelete = {
                        viewModel.deleteSchedule(schedule) // Xóa lịch trình
                    },
                    onUpdate = {
                        viewModel.updateSchedule(it) // Cập nhật lịch trình
                    }
                )
            }
        }
    }
}

@Composable
fun HeaderScreen(
    modifier: Modifier,
    title: String,
    onClickAdd: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth(1f)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = modifier,
            textAlign = TextAlign.Center,
            text = title,
            maxLines = 1,
            style = MaterialTheme.typography.displayLarge
        )

        IconButton(
            onClick = onClickAdd,
            modifier = modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "Click to Add",
                modifier.size(50.dp)
            )
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun AddSchedule(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onSave: (Schedule) -> Unit
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

    var isShowFreqDialog by remember { mutableStateOf(false) } // có mở dialog tần suất k
    var isShowTimeDialog by remember { mutableStateOf(false) } // có mở dialog thời gian k
    var showDate by remember { mutableIntStateOf(0) } // kiểu tần suất ( 0,1,2,3)
    var isShowDateDialog by remember { mutableStateOf(false) } // có mở dialog ngày k

    AlertDialog(
        onDismissRequest = { onDismiss() },
        text = {
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = modifier.fillMaxWidth(),
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Ghi chú") },
                    placeholder = { Text("Viết ghi chú vào đây") },
                )
                Spacer(modifier = modifier.height(6.dp))

                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = modifier,
                        text = "Thời gian: "
                    )
                    Text(
                        modifier = modifier.clickable {
                            isShowTimeDialog = true
                        },
                        text = time
                    )
                }

                if (isShowTimeDialog) {
                    ShowPickTimeDialog(
                        time = time,
                        onSave = { newTime -> time = newTime },
                        onDismiss = { isShowTimeDialog = false }
                    )
                }

                Spacer(modifier = modifier.height(6.dp))

                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = modifier,
                        text = "Lặp lại: "
                    )
                    Text(
                        modifier = modifier.clickable { isShowFreqDialog = true },
                        text = freq.desc
                    )
                    if (isShowFreqDialog) {
                        FrequencyDialog(
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
                            onDismiss = { isShowFreqDialog = false }
                        )
                    }
                }

                Spacer(modifier = modifier.height(6.dp))

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
                                text = "Ngày thực hiện: "
                            )
                            Text(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .clickable { isShowDateDialog = true },
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
                                text = "Ngày bắt đầu: "
                            )
                            Text(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .clickable { isShowDateDialog = true },
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
                                .clickable { isShowDateDialog = true },
                            text = "Từ $startDate đến hết $endDate"
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
                                text = "Số ngày đã chọn: "
                            )
                            Text(
                                modifier = modifier.clickable {
                                    isShowDateDialog = true
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
                            onDismiss = { isShowDateDialog = false }
                        )

                        1 -> ShowPickDateDialog(
                            date = startDate,
                            onSave = { newDate -> startDate = newDate },
                            onDismiss = { isShowDateDialog = false }
                        )

                        2 -> ShowPickDateRangeDialog(
                            modifier = modifier,
                            onSave = { s, e ->
                                startDate = s
                                endDate = e
                            },
                            onDismiss = { isShowDateDialog = false }
                        )

                        3 -> ShowPickMultipleDateDialog(
                            modifier = modifier,
                            nDays = numDays.toString(),
                            onSave = { dates ->
                                selectedDates = dates
                            },
                            onDismiss = { isShowDateDialog = false }
                        )
                    }
                }
            }
            Spacer(modifier = modifier.height(6.dp))
        },
        confirmButton = {
            Button(
                modifier = modifier,
                onClick = {
                    val newSchedule =
                        Schedule(
                            note = note,
                            time = time,
                            frequency = freq.desc,
                            dateStart = if (showDate == 0 || showDate == 1 || showDate == 2) startDate else "",
                            dateEnd = if (showDate == 2) endDate else "",
                            pickedDate = if (showDate == 3) selectedDates else emptyList(),
                            isEnabled = true
                        )
                    onSave(newSchedule)
                }) {
                Text("Lưu")
            }
        },
        dismissButton = {
            Button(
                modifier = modifier,
                onClick = { onDismiss() }) {
                Text("Hủy")
            }
        }
    )
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview(modifier: Modifier = Modifier) {
    LifePlanTheme {

    }
}