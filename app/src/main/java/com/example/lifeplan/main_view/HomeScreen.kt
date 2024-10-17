package com.example.lifeplan.main_view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeplan.R
import com.example.lifeplan.custom.view.CustomMonthCalendar
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@Composable
fun HomeScreen() {
    val numOfThingToDoThisWeek by remember { mutableIntStateOf(0) }
    val numOfThingToDoNextWeek by remember { mutableIntStateOf(0) }
    val numOfRemainDayofWeek by remember { mutableIntStateOf(0) }
    val totalExpenseOfWeek by remember { mutableIntStateOf(0) }
    val typeMoney by remember { mutableStateOf("VNĐ") }

    // tháng hiện tại
    val currentMonth =
        LocalDate.now().format(DateTimeFormatter.ofPattern("MM/yyyy", Locale.getDefault()))

    // Tìm ngày đầu tiên của tuần trước (Thứ 2)
    val firstDayOfLastWeek = LocalDate.now()
        .minusWeeks(1)
        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    // Tìm ngày cuối cùng của tuần trước (Chủ Nhật)
    val lastDayOfLastWeek = LocalDate.now()
        .minusWeeks(1)
        .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

    // Thời gian của tuần trước
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
    val lastWeek =
        "${firstDayOfLastWeek.format(formatter)} - ${lastDayOfLastWeek.format(formatter)}"

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.home),
            textAlign = TextAlign.Center,
            maxLines = 1,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.overview),
            maxLines = 1,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.this_week),
                maxLines = 1,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = stringResource(
                        R.string.description_of_this_week,
                        numOfThingToDoThisWeek,
                        numOfRemainDayofWeek
                    ),
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(
                        R.string.description_expense_this_week,
                        totalExpenseOfWeek,
                        typeMoney
                    ),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.next_week),
                maxLines = 1,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp)
            )

            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(
                    R.string.description_of_next_week,
                    numOfThingToDoNextWeek
                ),
                style = MaterialTheme.typography.labelLarge
            )
        }

        HorizontalDivider(
            thickness = 1.dp,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = stringResource(R.string.report),
            maxLines = 1,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = stringResource(R.string.overview_schedule, currentMonth),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomMonthCalendar(currentMonth, mapOf())

        HorizontalDivider(
            thickness = 1.dp,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = stringResource(R.string.last_week_report, lastWeek),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    locale = "vi"
)
@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    locale = "en"
)
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}