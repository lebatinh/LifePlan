package com.example.lifeplan.main_view

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lifeplan.R

@Composable
fun ExpenditureScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        HeaderScreen(
            modifier = modifier,
            title = stringResource(R.string.Expenditure)
        ) {
            // Sự kiện thêm giao dịch vào sổ

        }

        Spacer(modifier = modifier.height(16.dp))

        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.large
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null)

                Row(
                    modifier = modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                    Spacer(modifier = modifier.padding(4.dp))
                    Text(
                        modifier = modifier,
                        text = "Tháng 10/2024",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }

            Spacer(modifier = modifier.height(16.dp))

            Row(
                modifier = modifier.fillMaxWidth(),
                Arrangement.Center,
                Alignment.CenterVertically
            ) {

            }
        }
    }

}

@Composable
fun ExpenditureBox(
    modifier: Modifier = Modifier,
    typeExpenditure: Boolean,
    money: String,
    isUp: Boolean,
    diferrentOfMoney: String
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
            .border(
                2.dp,
                if (typeExpenditure) MaterialTheme.colorScheme.outline
                else MaterialTheme.colorScheme.outlineVariant
            ),
        Arrangement.Center,
        Alignment.CenterHorizontally
    ) {
        Row {
            Icon(Icons.Default.DateRange, contentDescription = null)
            Spacer(modifier = modifier.padding(4.dp))
            Text(stringResource(R.string.expenditure), style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = modifier.padding(8.dp))

        Row {
            Text(money, style = MaterialTheme.typography.bodyLarge)
            Text("vnđ", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = modifier.padding(8.dp))

        Row {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                modifier = modifier.rotate(if (isUp) 270f else 90f),
                contentDescription = null,
                tint = if ((typeExpenditure && isUp) || (!typeExpenditure && !isUp)) MaterialTheme.colorScheme.outline
                else MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(modifier = modifier.padding(4.dp))
            Text(
                stringResource(R.string.diferrentOfMoney, diferrentOfMoney),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showSystemUi = true, showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ExpenditureScreenPreview() {
    ExpenditureScreen()
}