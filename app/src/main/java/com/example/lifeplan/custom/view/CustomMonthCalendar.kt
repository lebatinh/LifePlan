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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lifeplan.R
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CustomMonthCalendar(
    monthOfYear: String,  // Tháng dưới dạng chuỗi MM/yyyy
    events: Map<String, String>  // Sự kiện dưới dạng Map<String, String>
) {
    // Chuyển đổi monthOfYear thành YearMonth
    val (month, year) = monthOfYear.split("/").map { it.toInt() }
    val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
    val firstDayOfMonth = LocalDate.of(year, month, 1).dayOfWeek.value

    Box(
        modifier = Modifier
            .padding(8.dp)
            .border(2.dp, MaterialTheme.colorScheme.onBackground)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
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

            LazyVerticalGrid(
                columns = GridCells.Fixed(7), modifier = Modifier.height(350.dp)
            ) {
                items(firstDayOfMonth - 1) {
                    Text(text = "", modifier = Modifier.height(50.dp))
                }

                items(daysInMonth) { day ->
                    val dateString = "${"%02d".format(day + 1)}/$month/$year"
                    val eventLabel = events[dateString]

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
@Composable
fun CustomCalenderPreview() {
    val event = mapOf(
        "11/10/2024" to "13 sự kiện",
        "12/10/2024" to "1 sự kiện",
        "13/10/2024" to "32 sự kiện"
    )
    CustomMonthCalendar(
        monthOfYear = "10/2024",
        events = event
    )
}