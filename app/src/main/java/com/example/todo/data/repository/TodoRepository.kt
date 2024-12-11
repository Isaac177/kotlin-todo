package com.example.todo.data.repository
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.todo.data.local.TodoDao
import com.example.todo.data.model.Todo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoDao: TodoDao
) {
    fun getTodosByUser(userId: Int): Flow<List<Todo>> {
        return todoDao.getTodosByUser(userId)
    }

    fun getTodoById(id: Int): Flow<Todo?> {
        return todoDao.getTodoById(id)
    }

    suspend fun addTodo(todo: Todo): Long {
        return todoDao.insertTodo(todo)
    }

    suspend fun updateTodo(todo: Todo) {
        todoDao.updateTodo(todo)
    }

    suspend fun deleteTodo(todo: Todo) {
        todoDao.deleteTodo(todo)
    }

    fun getPendingTodos(userId: Int): Flow<List<Todo>> {
        return todoDao.getPendingTodos(userId)
    }

    fun getCompletedTodos(userId: Int): Flow<List<Todo>> {
        return todoDao.getCompletedTodos(userId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getUpcomingTodos(): List<Todo> {
        val now = LocalDateTime.now()
        val upcoming = now.plusDays(1) // Get todos due in next 24 hours
        return todoDao.getUpcomingTodos(now, upcoming)
    }
}
