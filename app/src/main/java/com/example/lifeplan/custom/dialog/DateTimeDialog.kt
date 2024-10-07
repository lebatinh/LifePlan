package com.example.lifeplan.custom.dialog

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.lifeplan.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
    modifier: Modifier,
    onSave: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val selectDate = stringResource(R.string.select_date)
    var startDate by remember { mutableStateOf(selectDate) }
    var endDate by remember { mutableStateOf(selectDate) }

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
                Text(stringResource(R.string.save))
            }
        },
        title = { Text(stringResource(R.string.select_start_and_end_date)) },
        text = {
            Column(
                modifier = modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .clickable {
                            isShowStartDatePicker = true
                        }
                ) {
                    Text(stringResource(R.string.start_date))
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
                        .clickable {
                            isShowEndDatePicker = true
                        }
                ) {
                    Text(
                        text = stringResource(R.string.end_date)
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
                Text(stringResource(R.string.cancel))
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

@SuppressLint("DefaultLocale")
@Composable
fun ShowPickMultipleDateDialog(// Chọn danh sách thời gian ngày/tháng/năm riêng biệt
    modifier: Modifier,
    nDays: String,
    onSave: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var numberOfDays by remember { mutableStateOf(nDays) }

    // Sử dụng Set để tự động loại bỏ trùng lặp
    val selectedDates = remember { mutableSetOf<String>() }

    var showDatePicker by remember { mutableStateOf<Int?>(null) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = {
                // Kiểm tra số lượng ngày chọn và lưu nếu hợp lệ
                if (selectedDates.size == numberOfDays.toInt() && selectedDates.isNotEmpty()) {
                    val sortedDates = selectedDates
                        .sortedBy { LocalDate.parse(it, DateTimeFormatter.ofPattern("dd/MM/yyyy")) }
                    onSave(sortedDates) // Lưu danh sách đã sắp xếp
                    onDismiss()
                }
            }) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(stringResource(R.string.cancel))
            }
        },
        title = { Text(stringResource(R.string.select_a_schedule_date)) },
        text = {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .scrollable(
                        enabled = true, state = rememberScrollState(),
                        orientation = Orientation.Vertical
                    ),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = numberOfDays,
                    onValueChange = { n ->
                        if (n.all { it.isDigit() }) {
                            numberOfDays = n
                        }
                    },
                    label = { Text(stringResource(R.string.number_of_days_for_which_you_want_to_schedule)) },
                    modifier = modifier.fillMaxWidth()
                )

                val numDays = numberOfDays.toIntOrNull() ?: 0

                for (i in 0 until numDays) {
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                            .clickable {
                                showDatePicker = i
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.number_of_date, i + 1),
                            modifier = modifier
                        )
                        val date =
                            if (selectedDates.size > i) selectedDates.elementAt(i) else stringResource(
                                R.string.click_to_select_date
                            )
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

                if (!selectedDates.contains(formattedDate)) {
                    if (index < selectedDates.size) {
                        // Nếu là chỉnh sửa ngày đã chọn, thay thế ngày mới
                        selectedDates.remove(selectedDates.elementAt(index))
                        selectedDates.add(formattedDate)
                    } else {
                        // Thêm ngày mới vào danh sách
                        selectedDates.add(formattedDate)
                    }
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