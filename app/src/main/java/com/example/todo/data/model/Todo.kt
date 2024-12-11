package com.example.todo.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "todos",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class Todo @RequiresApi(Build.VERSION_CODES.O) constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val dueDate: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
