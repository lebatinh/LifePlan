package com.example.lifeplan.custom_dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier


@Composable
fun NoteDialog(
    modifier: Modifier = Modifier,
    note: String?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var editNote by remember { mutableStateOf(note ?: "") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        text = {
            Column {
                OutlinedTextField(
                    value = editNote,
                    onValueChange = { newText -> editNote = newText },
                    placeholder = { Text("Viết ghi chú vào đây") },
                    modifier = modifier.fillMaxWidth(),
                    label = { Text("Ghi chú") },
                    shape = MaterialTheme.shapes.small
                )
            }

        },
        confirmButton = {
            Button(
                onClick = {
                    //Lưu và đóng dialog
                    onSave(editNote)
                    onDismiss()
                }
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    //click để hủy
                    onDismiss()
                }
            ) {
                Text("Hủy")
            }
        }
    )
}