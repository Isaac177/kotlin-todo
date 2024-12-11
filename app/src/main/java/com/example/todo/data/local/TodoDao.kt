package com.example.todo.data.local
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.todo.data.model.Todo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos WHERE userId = :userId ORDER BY createdAt DESC")
    fun getTodosByUser(userId: Int): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE id = :id LIMIT 1")
    fun getTodoById(id: Int): Flow<Todo?>

    @Insert
    suspend fun insertTodo(todo: Todo): Long

    @Update
    suspend fun updateTodo(todo: Todo)

    @Delete
    suspend fun deleteTodo(todo: Todo)

    @Query("""
        SELECT * FROM todos 
        WHERE userId = :userId AND isCompleted = 0 
        ORDER BY 
            CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END,
            dueDate ASC
    """)
    fun getPendingTodos(userId: Int): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE userId = :userId AND isCompleted = 1 ORDER BY updatedAt DESC")
    fun getCompletedTodos(userId: Int): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE isCompleted = 0 AND dueDate IS NOT NULL AND dueDate > :now AND dueDate <= :upcoming ORDER BY dueDate ASC")
    suspend fun getUpcomingTodos(now: LocalDateTime, upcoming: LocalDateTime): List<Todo>
}
