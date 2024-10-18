package com.example.lifeplan.main_view

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import kotlin.math.abs

@Composable
fun HomeScreen() {
    val numOfThingToDoThisWeek by remember { mutableIntStateOf(0) } // số việc làm trong tuần hiện tại
    val numOfThingToDoNextWeek by remember { mutableIntStateOf(0) } // số việc làm trong tuần tiếp theo
    val numOfRemainDayofWeek by remember { mutableIntStateOf(0) } // số ngày còn lại trong tuần hiện tại
    val totalExpenseOfThisWeek by remember { mutableIntStateOf(0) } // tổng chi tiêu trong tuần hiện tại
    val totalExpenseOfLastWeek by remember { mutableIntStateOf(0) } // tổng chi tiêu trong tuần trước
    val totalExpenseOfLastOfLastWeek by remember { mutableIntStateOf(0) } // tổng chi tiêu của 2 tuần trước đó
    val typeMoney by remember { mutableStateOf("VNĐ") } // đơn vị tiền tệ
    val typeHighestExpense by remember { mutableStateOf("") } // mục chi tiêu cao nhất trong tuần trước
    val highestExpense by remember { mutableIntStateOf(0) } // số tiền của mục chi tiêu cao nhất trong tuần trước
    val highestTransacsion by remember { mutableIntStateOf(0) } // số tiền của giao dịch lớn nhất tuần trước
    val timeHighestTransacsion by remember { mutableStateOf("") } // thời gian của giao dịch lớn nhất tuần trước

    val percentUpExpense = if (totalExpenseOfLastOfLastWeek != 0) {
        abs((totalExpenseOfLastWeek / totalExpenseOfLastOfLastWeek - 1) * 100)
    } else "" // tỉ lệ tăng/giảm trong tuần trước so với 2 tuần trước đó

    val isUpExpense =
        if ((totalExpenseOfLastWeek - totalExpenseOfLastOfLastWeek) > 0) stringResource(
            R.string.type_up_expense,
            percentUpExpense
        )
        else if ((totalExpenseOfLastWeek - totalExpenseOfLastOfLastWeek) < 0) stringResource(
            R.string.type_down_expense,
            percentUpExpense
        )
        else stringResource(R.string.type_equal_expense) // kiểu chênh lệch chi tiêu tuần trước so với 2 tuần trước đó

    val percentHighestExpense =
        if (highestExpense != 0) {
            (highestExpense / totalExpenseOfLastWeek) * 100 // tỉ trọng của phần lớn nhất trong chi tiêu
        } else ""

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
                        totalExpenseOfThisWeek,
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

        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.onBackground,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            Text(
                text = stringResource(R.string.overview_expense_last_week),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                maxLines = 2,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ShowChart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                )
                Text(
                    text = stringResource(
                        R.string.total_expense_last_week,
                        totalExpenseOfLastWeek,
                        typeMoney,
                        isUpExpense
                    ),
                    maxLines = 3,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.width(24.dp)
                )
                Text(
                    text = stringResource(
                        R.string.type_highest_expense,
                        typeHighestExpense,
                        highestExpense,
                        typeMoney,
                        percentHighestExpense
                    ),
                    maxLines = 2,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.MonetizationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.width(24.dp)
                )
                Text(
                    text = stringResource(
                        R.string.highest_transacsion,
                        highestTransacsion,
                        typeMoney,
                        timeHighestTransacsion
                    ),
                    maxLines = 2,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.onBackground,
                    shape = MaterialTheme.shapes.medium
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = stringResource(
                    R.string.total_expense_last_week,
                    totalExpenseOfLastWeek,
                    typeMoney,
                    isUpExpense
                ),
                textAlign = TextAlign.Center,
                maxLines = 3,
                style = MaterialTheme.typography.labelLarge
            )

            // biểu đồ cột của tuần trước và 2 tuần trước

        }
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