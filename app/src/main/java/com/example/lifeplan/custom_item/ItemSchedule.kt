package com.example.lifeplan.custom_item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.sharp.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.lifeplan.R
import com.example.lifeplan.custom_dialog.FrequencyDialog
import com.example.lifeplan.custom_dialog.NoteDialog
import com.example.lifeplan.custom_dialog.ShowPickTimeDialog
import com.example.lifeplan.dao.Schedule
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ItemSchedule(
    modifier: Modifier,
    schedule: Schedule,
    onDelete: () -> Unit,
    onUpdate: (Schedule) -> Unit
) {
    var isMore by remember { mutableStateOf(false) } // mở rộng item lịch trình
    var freq by remember { mutableStateOf(schedule.frequency) } // tần suất

    var editNote by remember { mutableStateOf(schedule.note) } // ghi chú
    var isShowDialog by remember { mutableStateOf(false) } //có hiện dialog sửa ghi chú hay ko

    var timeSetting by remember { mutableStateOf(schedule.time) } // thời gian
    var isShowTimeDialog by remember { mutableStateOf(false) } // hiện dialog chọn thời gian hay ko

    var dateStart by remember { mutableStateOf(schedule.dateStart ?: "") } // ngày bắt đầu
    var dateEnd by remember { mutableStateOf(schedule.dateEnd ?: "") } // ngày kết thúc
    var pickedDate by remember {
        mutableStateOf(
            schedule.pickedDate ?: emptyList()
        )
    } // ngày riêng lẻ

    var isRepeat by remember { mutableStateOf(schedule.isEnabled) } // có bật switch để báo ko

    var selectedFrequency by remember { mutableStateOf(FrequencyItems.fromString(schedule.frequency)) } // lưu tần suất
    var showDialogFreq by remember { mutableStateOf(false) } // hiện dialog tần suất hay ko

    var showDate by remember { mutableIntStateOf(0) } // mở dialog tùy giá trị (0,1,2,3)
    LaunchedEffect(schedule) {
        dateStart = schedule.dateStart ?: ""
        dateEnd = schedule.dateEnd ?: ""
        pickedDate = schedule.pickedDate ?: emptyList()
    }
    val updatedSchedule by remember {
        derivedStateOf {
            schedule.copy(
                note = editNote,
                time = timeSetting,
                frequency = freq,
                dateStart = dateStart,
                dateEnd = dateEnd,
                pickedDate = pickedDate,
                isEnabled = isRepeat
            )
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { isMore = !isMore },
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = modifier.fillMaxWidth()
            ) {
                if (isMore) {

                    Icon(
                        modifier = modifier
                            .size(24.dp)
                            .align(Alignment.CenterVertically)
                            .animateContentSize(),
                        bitmap = ImageBitmap.imageResource(R.drawable.tag),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = editNote,
                    modifier = modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .animateContentSize()
                        .clickable(isMore) {
                            //click để tạo dialog sửa note
                            isShowDialog = true
                        },
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = if (isMore) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start
                )

                if (isShowDialog) {
                    NoteDialog(
                        modifier = modifier.fillMaxWidth(),
                        note = editNote,
                        onSave = { newNote ->
                            editNote = newNote
                            // cập nhật lại note
                            onUpdate(updatedSchedule.copy(note = editNote))
                        },
                        onDismiss = { isShowDialog = false }
                    )
                }

                val rotationAngle by animateFloatAsState(
                    targetValue = if (isMore) 180f else 0f, label = "rotate"
                )
                IconButton(
                    modifier = modifier.align(Alignment.CenterVertically),
                    onClick = {
                        isMore = !isMore
                    }
                ) {
                    Icon(
                        imageVector = Icons.Sharp.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = modifier
                            .rotate(rotationAngle)
                    )
                }
            }

            Text(
                text = timeSetting,
                style = MaterialTheme.typography.displayMedium,
                modifier = modifier.clickable {
                    //click để chọn thời gian
                    isShowTimeDialog = true
                }
            )

            if (isShowTimeDialog) {
                ShowPickTimeDialog(
                    time = timeSetting,
                    onSave = { newTime ->
                        timeSetting = newTime
                        onUpdate(updatedSchedule.copy(time = timeSetting))
                    },
                    onDismiss = { isShowTimeDialog = false }
                )
            }
            Row(
                modifier = modifier
                    .fillMaxWidth()
            ) {
                Text(
                    // hiển thị tần suất
                    text = String.format(
                        "%s" + stringResource(R.string.notification_of_event),
                        if (isRepeat) stringResource(R.string.turning_on) else stringResource(R.string.turning_off)
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )

                Switch(
                    checked = isRepeat,
                    onCheckedChange = {
                        isRepeat = it
                        // cập nhật lại tần suất
                        onUpdate(updatedSchedule.copy(isEnabled = it))
                    }
                )
            }

            AnimatedVisibility(isMore) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = modifier,
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary
                        )

                        Spacer(modifier = modifier.width(8.dp))

                        Text(
                            // hiển thị tần suất, click để chọn tần suất
                            text = when (selectedFrequency) {
                                FrequencyItems.ONCE -> stringResource(
                                    R.string.only_announced_at,
                                    dateStart
                                )

                                FrequencyItems.DAILY -> stringResource(
                                    R.string.announcement_every_day_at, dateStart.substring(
                                        0..4
                                    )
                                )

                                FrequencyItems.WEEKLY -> stringResource(
                                    R.string.weekly_announcement_at, getDayOfWeek(
                                        dateStart
                                    )
                                )

                                FrequencyItems.MONTHLY -> stringResource(
                                    R.string.announcement_every_month_at, dateStart.substring(
                                        0..4
                                    )
                                )

                                FrequencyItems.YEARLY -> stringResource(
                                    R.string.announcement_every_year_at, dateStart.substring(
                                        0..4
                                    )
                                )

                                FrequencyItems.DATETODATE -> stringResource(
                                    R.string.announcement_from_to,
                                    dateStart,
                                    dateEnd
                                )

                                FrequencyItems.PICKDATE -> stringResource(
                                    R.string.notice_in_days,
                                    pickedDate.size
                                )
                            },
                            modifier = modifier.clickable {
                                //click để chọn tần suất
                                showDialogFreq = true
                            }
                        )
                        showDate = when (selectedFrequency) {
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
                        if (showDialogFreq) {
                            FrequencyDialog(
                                modifier = modifier,
                                frequency = selectedFrequency,
                                onFrequencyItems = { newFrequency ->
                                    selectedFrequency = newFrequency
                                    freq = selectedFrequency.desc
                                    // Đặt lại các biến ngày không liên quan
                                    when (selectedFrequency) {
                                        FrequencyItems.ONCE,
                                        FrequencyItems.DAILY,
                                        FrequencyItems.WEEKLY,
                                        FrequencyItems.MONTHLY,
                                        FrequencyItems.YEARLY -> {
                                            dateEnd = ""
                                            pickedDate = emptyList()
                                        }

                                        FrequencyItems.DATETODATE -> {
                                            pickedDate = emptyList()
                                        }

                                        FrequencyItems.PICKDATE -> {
                                            dateStart = ""
                                            dateEnd = ""
                                        }
                                    }
                                },
                                onSaveDateStart = {
                                    dateStart = it
                                    onUpdate(updatedSchedule.copy(dateStart = dateStart))
                                },
                                onSaveDateEnd = {
                                    dateEnd = it
                                    onUpdate(updatedSchedule.copy(dateEnd = dateEnd))
                                },
                                onDateSelected = {
                                    pickedDate = it
                                    onUpdate(updatedSchedule.copy(pickedDate = pickedDate))
                                },
                                onDismiss = { showDialogFreq = false }
                            )
                        }
                    }

                    Spacer(modifier = modifier.height(12.dp))

                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = modifier,
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = modifier.width(8.dp))

                        Text(
                            text = stringResource(R.string.delete),
                            modifier = modifier.clickable {
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}

fun getDayOfWeek(dateStr: String, pattern: String = "dd/MM/yyyy"): String {
    // Chuyển chuỗi ngày thành LocalDate
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    val date = LocalDate.parse(dateStr, formatter)

    // Lấy đối tượng DayOfWeek từ LocalDate
    val dayOfWeek: DayOfWeek = date.dayOfWeek

    // Trả về tên ngày trong tuần (thứ)
    return dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Locale("vi"))
}