package com.example.lifeplan.custom_dialog

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@SuppressLint("DefaultLocale")
@Composable
fun ShowPickTimeDialog( // Chọn thời gian giờ:phút
    time: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance()
    val hourNow = calendar.get(Calendar.HOUR_OF_DAY)
    val minuteNow = calendar.get(Calendar.MINUTE)

    var hourPicked by remember { mutableIntStateOf(hourNow) } // chọn giờ
    var minutePicked by remember { mutableIntStateOf(minuteNow) } // chọn phút

    var timeDisplay by remember { mutableStateOf(time) } // thời gian hiển thị

    val context = LocalContext.current

    TimePickerDialog(
        context,
        { _, hourOfDay: Int, minute: Int ->
            hourPicked = hourOfDay
            minutePicked = minute

            timeDisplay = String.format("%02d:%02d", hourPicked, minutePicked)

            onSave(timeDisplay)
            onDismiss()
        },
        hourPicked,
        minutePicked,
        true // định dạng 24h
    ).show()
}

@SuppressLint("DefaultLocale")
@Composable
fun ShowPickDateDialog( // Chọn thời gian ngày/tháng/năm 1 lần
    date: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance()
    val dayNow = calendar.get(Calendar.DAY_OF_MONTH)
    val monthNow = calendar.get(Calendar.MONTH) + 1
    val yearNow = calendar.get(Calendar.YEAR)

    var dayPicked by remember { mutableIntStateOf(dayNow) }
    var monthPicked by remember { mutableIntStateOf(monthNow) }
    var yearPicked by remember { mutableIntStateOf(yearNow) }

    var dateDisplay by remember { mutableStateOf(date) }

    val context = LocalContext.current

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year: Int, month: Int, dayOfMonth: Int ->
            dayPicked = dayOfMonth
            monthPicked = month + 1
            yearPicked = year

            dateDisplay = String.format("%02d/%02d/%04d", dayPicked, monthPicked, yearPicked)

            onSave(dateDisplay)
            onDismiss()
        },
        yearPicked,
        monthPicked - 1,
        dayPicked
    )

    // đặt giới hạn chọn từ ngày hiện tại trở đi
    datePickerDialog.datePicker.minDate = calendar.timeInMillis

    datePickerDialog.show()
}

@Composable
fun ShowPickDateRangeDialog(// Chọn thời gian ngày/tháng/năm bắt đầu và kết thúc (khoảng)
    modifier: Modifier = Modifier,
    onSave: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var startDate by remember { mutableStateOf("Click để chọn ngày bắt đầu") }
    var endDate by remember { mutableStateOf("Click để chọn ngày kết thúc") }

    var isShowStartDatePicker by remember { mutableStateOf(false) }
    var isShowEndDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = {
                if (isDateValid(startDate, endDate)) {
                    onSave(startDate, endDate)
                    onDismiss()
                }

            }) {
                Text("Lưu")
            }
        },
        title = { Text("Chọn ngày bắt đầu và kết thúc") },
        text = {
            Column(
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            isShowStartDatePicker = true
                        }
                ) {
                    Text(
                        text = "Ngày bắt đầu: "
                    )
                    Spacer(modifier = modifier.width(8.dp))

                    Text(
                        text = startDate,
                        modifier = modifier
                    )

                    // Hộp thoại chọn ngày bắt đầu
                    if (isShowStartDatePicker) {
                        ShowPickDateDialog(date = startDate, onSave = { sDate ->
                            startDate = sDate
                            isShowStartDatePicker = false

                        }, onDismiss = { isShowStartDatePicker = false })
                    }
                }
                Spacer(modifier = modifier.height(8.dp))
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            isShowEndDatePicker = true
                        }
                ) {
                    Text(
                        text = "Ngày kết thúc: "
                    )
                    Spacer(modifier = modifier.width(8.dp))

                    Text(
                        text = endDate,
                        modifier = modifier
                    )
                    // Hộp thoại chọn ngày kết thúc
                    if (isShowEndDatePicker) {
                        ShowPickDateDialog(date = endDate, onSave = { eDate ->
                            endDate = eDate
                            isShowEndDatePicker = false
                        }, onDismiss = { isShowEndDatePicker = false })
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Hủy")
            }
        }
    )
}

fun isDateValid(startDate: String, endDate: String): Boolean {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return try {
        val start = format.parse(startDate)
        val end = format.parse(endDate)
        start!!.before(end) || start == end
    } catch (e: Exception) {
        false
    }
}

@SuppressLint("DefaultLocale", "SuspiciousIndentation")
@Composable
fun ShowPickMultipleDateDialog( // Chọn thời gian ngày/tháng/năm nhiều lần (danh sách)
    modifier: Modifier = Modifier,
    nDays: String,
    onSave: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var numberOfDays by remember { mutableStateOf(nDays) }

    val selectedDates = remember { mutableListOf<String>() }

    var showDatePicker by remember { mutableStateOf<Int?>(null) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = {
                if (selectedDates.size == numberOfDays.toInt() && selectedDates.isNotEmpty()) {
                    onSave(selectedDates)
                    onDismiss()
                }
            }) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Hủy")
            }
        },
        title = { Text("Chọn ngày lên lịch") },
        text = {
            Column(
                modifier = modifier.padding(8.dp)
            ) {
                OutlinedTextField(
                    value = numberOfDays,
                    onValueChange = { n ->
                        if (n.all { it.isDigit() }) {
                            numberOfDays = n
                        }
                    },
                    label = { Text("Số ngày mà bạn muốn đặt lịch") },
                    modifier = modifier.fillMaxWidth()
                )

                Spacer(modifier = modifier.height(12.dp))

                val numDays = numberOfDays.toIntOrNull() ?: 0

                for (i in 0 until numDays) {
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                showDatePicker = i
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ngày thứ ${i + 1}: ",
                            modifier = modifier.weight(1f)
                        )
                        val date =
                            if (selectedDates.size > i) selectedDates[i] else "Click để chọn ngày"
                        Text(text = date, modifier = modifier)
                    }
                }
            }
        }
    )
    // Show DatePickerDialog when needed
    showDatePicker?.let { index ->

        val calendar = Calendar.getInstance()
        val dayNow = calendar.get(Calendar.DAY_OF_MONTH)
        val monthNow = calendar.get(Calendar.MONTH) + 1
        val yearNow = calendar.get(Calendar.YEAR)

        var dayPicked by remember { mutableIntStateOf(dayNow) }
        var monthPicked by remember { mutableIntStateOf(monthNow) }
        var yearPicked by remember { mutableIntStateOf(yearNow) }

        val datePickerDialog = DatePickerDialog(
            LocalContext.current,
            { _, year: Int, month: Int, dayOfMonth: Int ->
                dayPicked = dayOfMonth
                monthPicked = month + 1
                yearPicked = year

                val formattedDate =
                    String.format("%02d/%02d/%04d", dayPicked, monthPicked, yearPicked)

                if (index < selectedDates.size) {
                    selectedDates[index] = formattedDate
                } else {
                    selectedDates.add(formattedDate)
                }
                showDatePicker = null
            },
            yearPicked,
            monthPicked - 1,
            dayPicked
        )

        // đặt giới hạn chọn từ ngày hiện tại trở đi
        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        datePickerDialog.show()
    }
}