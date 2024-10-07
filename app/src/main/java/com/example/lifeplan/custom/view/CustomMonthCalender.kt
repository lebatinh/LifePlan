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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CustomMonthCalender(
    year: Int,
    month: Int,
    events: Map<LocalDate, String>
) {
    val daysInMonth = YearMonth.of(year, month).lengthOfMonth() // số ngày trong tháng
    val firstDayofMonth = LocalDate.of(year, month, 1).dayOfWeek.value // thứ đầu tiên của tháng

    Box(
        modifier = Modifier
            .padding(8.dp)
            .border(2.dp, MaterialTheme.colorScheme.onBackground)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row {
                listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach {
                    Text(
                        text = it,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            LazyVerticalGrid(columns = GridCells.Fixed(7)) {
                items(firstDayofMonth - 1) {
                    Text(text = "", modifier = Modifier.height(50.dp))
                }

                items(daysInMonth) { day ->
                    val date = LocalDate.of(year, month, day + 1)
                    val eventLabel = events[date]

                    Column(
                        modifier = Modifier
                            .height(60.dp)
                            .padding(4.dp)
                            .background(if (eventLabel != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.background),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = (day + 1).toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (eventLabel != null) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onBackground
                        )
                        eventLabel?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.surface,
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
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun CustomCalenderPreview() {
    val events = mapOf(
        LocalDate.of(2024, 10, 11) to "13 sự kiện",
        LocalDate.of(2024, 10, 15) to "1 sự kiện",
        LocalDate.of(2024, 10, 20) to "32 sự kiện"
    )
    CustomMonthCalender(
        year = 2024,
        month = 10,
        events = events
    )
}