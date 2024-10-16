package com.example.lifeplan.custom.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp

@Composable
fun CategoryItem(
    image: ImageVector,
    text: String,
    money: Int,
    typeMoney: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { onClick() },
        Arrangement.SpaceBetween,
        Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(imageVector = image, contentDescription = null, alignment = Alignment.Center)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = text, style = MaterialTheme.typography.labelLarge, maxLines = 1)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$money$typeMoney",
                style = MaterialTheme.typography.labelLarge.copy(fontStyle = FontStyle.Normal),
                maxLines = 1
            )
            Spacer(modifier = Modifier.width(4.dp))
            Image(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                alignment = Alignment.Center
            )
        }
    }
}
