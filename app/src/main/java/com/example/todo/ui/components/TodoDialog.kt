package com.example.todo.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todo.data.model.Todo
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDialog(
        todo: Todo? = null,
        onDismiss: () -> Unit,
        onConfirm: (String, String, LocalDateTime?) -> Unit
) {
    var title by remember { mutableStateOf(todo?.title ?: "") }
    var description by remember { mutableStateOf(todo?.description ?: "") }
    var dueDate by remember { mutableStateOf<LocalDateTime?>(todo?.dueDate) }
    var showDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy") }

    AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(if (todo == null) "Add New Todo" else "Edit Todo") },
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
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                                text = dueDate?.format(dateFormatter) ?: "No due date",
                                style = MaterialTheme.typography.bodyMedium
                        )
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, "Select date")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                        onClick = {
                            if (title.isNotBlank()) {
                                onConfirm(title, description, dueDate)
                            }
                        }
                ) { Text(if (todo == null) "Add" else "Save") }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )

    if (showDatePicker) {
        val datePickerState =
                rememberDatePickerState(
                        initialSelectedDateMillis =
                                dueDate?.toLocalDate()?.toEpochDay()?.times(24 * 60 * 60 * 1000)
                )

        DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    dueDate =
                                            LocalDateTime.of(
                                                    LocalDate.ofEpochDay(
                                                            millis / (24 * 60 * 60 * 1000)
                                                    ),
                                                    LocalTime.of(23, 59, 59)
                                            )
                                }
                                showDatePicker = false
                            }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
        ) { DatePicker(state = datePickerState) }
    }
}
