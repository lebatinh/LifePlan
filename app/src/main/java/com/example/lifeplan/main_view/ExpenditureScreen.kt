package com.example.lifeplan.main_view

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lifeplan.R
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ExpenditureScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        val expense by remember { mutableIntStateOf(0) }
        val income by remember { mutableIntStateOf(0) }
        val typeMoney by remember { mutableStateOf("vnđ") }
        var currentDate by remember { mutableStateOf(LocalDate.now()) }
        // Định dạng tháng/năm
        val formatter = DateTimeFormatter.ofPattern("MM/yyyy", Locale.getDefault())
        val formattedDate = currentDate.format(formatter)

        HeaderScreen(
            modifier = Modifier,
            title = stringResource(R.string.Expenditure)
        ) {
            // Sự kiện thêm giao dịch vào sổ

        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            shape = MaterialTheme.shapes.large
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        currentDate = currentDate.minusMonths(1)
                    }
                )

                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        modifier = Modifier,
                        text = formattedDate,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        currentDate = currentDate.plusMonths(1)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                shape = MaterialTheme.shapes.small
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        // Chi tiêu
                        ExpenditureBox(
                            modifier = Modifier.weight(1f),
                            typeExpenditure = true,
                            money = expense.toString(),
                            isUp = false,
                            diferrentOfMoney = "1000",
                            typeMoney = typeMoney
                        )

                        // Thu nhập
                        ExpenditureBox(
                            modifier = Modifier.weight(1f),
                            typeExpenditure = false,
                            money = income.toString(),
                            isUp = true,
                            diferrentOfMoney = "1000",
                            typeMoney = typeMoney
                        )
                    }

                    Text(
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.CenterHorizontally),
                        text = stringResource(
                            R.string.differenceMoney,
                            income - expense,
                            typeMoney
                        ),
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    )
                }

            }

            HorizontalDivider(modifier = Modifier.padding(8.dp), thickness = 1.dp)


        }
    }
}

@Composable
fun ExpenditureBox(
    modifier: Modifier,
    typeExpenditure: Boolean, // true là Chi tiêu, false là Thu nhập
    money: String,
    isUp: Boolean, // true là Tăng, false là Giảm
    diferrentOfMoney: String,
    typeMoney: String
) {
    // Trạng thái cuộn
    val scrollState = rememberScrollState()

    // Tạo hiệu ứng tự động cuộn
    LaunchedEffect(Unit) {
        while (true) {
            // Scroll từ đầu tới cuối
            scrollState.animateScrollTo(scrollState.maxValue)
            // Dừng lại 1 chút trước khi cuộn lại từ đầu
            delay(2500L)
            // Cuộn lại từ đầu
            scrollState.animateScrollTo(0)
            // Lặp lại sau khi cuộn về đầu
            delay(2500L)
        }
    }
    // Điều chỉnh màu sắc của border dựa trên loại giao dịch (chi tiêu/thu nhập)
    val borderColor =
        if (typeExpenditure) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant

    // Điều chỉnh icon tùy loại giao dịch
    val icon =
        if (typeExpenditure) ImageBitmap.imageResource(R.drawable.icon_income) else ImageBitmap.imageResource(
            R.drawable.icon_expense
        )

    // Điều chỉnh màu sắc dựa trên loại giao dịch (chi tiêu/thu nhập) và hướng (tăng/giảm)
    val iconColor = if (typeExpenditure) {
        if (!isUp) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline
    } else {
        if (!isUp) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant
    }

    // Điều chỉnh text theo loại giao dịch
    val text =
        if (typeExpenditure) stringResource(R.string.expense) else stringResource(R.string.income)

    // Cắt góc tùy loại giao dịch
    val cornerShape = if (typeExpenditure) RoundedCornerShape(
        topStart = 8.dp,
        bottomStart = 8.dp
    ) else RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)

    // Tinh chỉnh góc xoay dựa trên loại giao dịch và hướng
    val iconRotation = if (isUp) 0f else 180f

    Column(
        modifier = Modifier
            .clip(cornerShape)
            .border(1.dp, borderColor, cornerShape)
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                bitmap = icon,
                contentDescription = null,
                tint = borderColor,
                modifier = Modifier.size(24.dp)
            )
            Text(
                modifier = Modifier,
                maxLines = 1,
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        Text(
            modifier = Modifier
                .horizontalScroll(state = scrollState)
                .align(Alignment.CenterHorizontally),
            text = "$money$typeMoney",
            maxLines = 1,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )

        Row(
            modifier = Modifier.horizontalScroll(state = scrollState),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.arrow_up),
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.rotate(iconRotation)
            )
            Text(
                maxLines = 1,
                text = stringResource(R.string.diferrentMoney, diferrentOfMoney, typeMoney),
                style = MaterialTheme.typography.labelMedium,
                color = iconColor
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun ExpenditureScreenPreview() {
    ExpenditureScreen()
}