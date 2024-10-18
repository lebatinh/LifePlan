package com.example.lifeplan.custom.view

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lifeplan.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun CustomWeekCalendar(
    week: String,  // Tuần dưới dạng chuỗi "dd/MM/yyyy - dd/MM/yyyy"
    events: Map<String, String>  // Sự kiện dưới dạng Map<String, String>
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // Tách chuỗi "dd/MM/yyyy - dd/MM/yyyy" thành ngày bắt đầu và kết thúc
    val (startDateString, endDateString) = week.split(" - ")
    val startDate = LocalDate.parse(startDateString, dateFormatter)
    val endDate = LocalDate.parse(endDateString, dateFormatter)

    // Tạo danh sách 7 ngày trong tuần từ ngày bắt đầu
    val daysInWeek = (0..ChronoUnit.DAYS.between(startDate, endDate)).map { startDate.plusDays(it) }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .border(2.dp, MaterialTheme.colorScheme.onBackground)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Hiển thị các thứ trong tuần
            Row {
                listOf(
                    stringResource(R.string.monday),
                    stringResource(R.string.tuesday),
                    stringResource(R.string.wednesday),
                    stringResource(R.string.thursday),
                    stringResource(R.string.friday),
                    stringResource(R.string.saturday),
                    stringResource(R.string.sunday)
                ).forEach {
                    Text(
                        text = it,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Hiển thị lịch của tuần dưới dạng lưới 7 ngày
            LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.height(60.dp)) {
                items(daysInWeek) { date ->
                    val dateString =
                        date.format(dateFormatter) // Chuyển đổi LocalDate thành chuỗi "dd/MM/yyyy"
                    val eventLabel = events[dateString] // Lấy sự kiện từ Map nếu có

                    Column(
                        modifier = Modifier
                            .height(60.dp)
                            .padding(4.dp)
                            .background(if (eventLabel != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${date.dayOfMonth}/${date.monthValue}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (eventLabel != null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                        )
                        eventLabel?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun CustomWeekCalenderPreview() {
    val event = mapOf(
        "15/10/2024" to "13 sự kiện",
        "16/10/2024" to "1 sự kiện",
        "17/10/2024" to "32 sự kiện"
    )
    CustomWeekCalendar(
        week = "14/10/2024 - 20/10/2024",
        events = event
    )
}