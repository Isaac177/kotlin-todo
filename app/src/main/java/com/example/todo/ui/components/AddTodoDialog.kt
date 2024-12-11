package com.example.todo.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoDialog(onDismiss: () -> Unit, onConfirm: (String, String, LocalDateTime?) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf<LocalDateTime?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")

    AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Add New Todo") },
            text = {
                Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                    )

                    OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                    )

                    Row(
                            modifier =
                                    Modifier.fillMaxWidth()
                                            .clickable { showDatePicker = true }
                                            .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                                text = dueDate?.format(dateFormatter) ?: "Set due date",
                                style = MaterialTheme.typography.bodyLarge
                        )
                        Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select date"
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                        onClick = {
                            if (title.isNotBlank()) {
                                onConfirm(title, description, dueDate)
                            }
                        },
                        enabled = title.isNotBlank()
                ) { Text("Add") }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        )
        
        DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val instant = java.time.Instant.ofEpochMilli(millis)
                            val date = instant.atZone(ZoneOffset.UTC).toLocalDate()
                            selectedDate = date
                            dueDate = LocalDateTime.of(date, java.time.LocalTime.MIDNIGHT)
                        }
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}
