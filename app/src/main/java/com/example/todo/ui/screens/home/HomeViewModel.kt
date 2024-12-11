package com.example.todo.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.local.PreferencesManager
import com.example.todo.data.model.Todo
import com.example.todo.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel
@Inject
constructor(
        private val todoRepository: TodoRepository,
        private val preferencesManager: PreferencesManager
) : ViewModel() {
    private val _state = MutableStateFlow(TodoState())
    val state: StateFlow<TodoState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesManager.userId.collectLatest { userId -> userId?.let { loadTodos(it) } }
        }
    }

    private fun loadTodos(userId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            todoRepository
                    .getTodosByUser(userId)
                    .catch { e -> _state.update { it.copy(error = e.message, isLoading = false) } }
                    .collect { todos ->
                        val filteredAndSortedTodos = filterAndSortTodos(todos)
                        _state.update { it.copy(todos = filteredAndSortedTodos, isLoading = false) }
                    }
        }
    }

    private fun filterAndSortTodos(todos: List<Todo>): List<Todo> {
        val currentState = _state.value

        var filteredTodos =
                if (currentState.searchQuery.isNotBlank()) {
                    todos.filter { it.title.contains(currentState.searchQuery, ignoreCase = true) }
                } else {
                    todos
                }

        filteredTodos =
                when (currentState.filterType) {
                    FilterType.ALL -> filteredTodos
                    FilterType.ACTIVE -> filteredTodos.filter { !it.isCompleted }
                    FilterType.COMPLETED -> filteredTodos.filter { it.isCompleted }
                }

        return when (currentState.sortType) {
            SortType.DATE_DESC -> filteredTodos.sortedByDescending { it.dueDate }
            SortType.DATE_ASC -> filteredTodos.sortedBy { it.dueDate }
            SortType.TITLE_DESC -> filteredTodos.sortedByDescending { it.title }
            SortType.TITLE_ASC -> filteredTodos.sortedBy { it.title }
        }
    }

    fun showDialog(todo: Todo? = null) {
        _state.update { it.copy(isDialogVisible = true, selectedTodo = todo) }
    }

    fun hideDialog() {
        _state.update { it.copy(isDialogVisible = false, selectedTodo = null) }
    }

    fun saveTodo(title: String, description: String, dueDate: LocalDateTime?) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                preferencesManager.userId.firstOrNull()?.let { userId ->
                    val currentState = _state.value
                    if (currentState.selectedTodo != null) {
                        // Update existing todo
                        val updatedTodo =
                                currentState.selectedTodo.copy(
                                        title = title,
                                        description = description,
                                        dueDate = dueDate
                                )
                        todoRepository.updateTodo(updatedTodo)
                    } else {
                        // Create new todo
                        val todo =
                                Todo(
                                        userId = userId,
                                        title = title,
                                        description = description,
                                        dueDate = dueDate
                                )
                        todoRepository.addTodo(todo)
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            } finally {
                _state.update {
                    it.copy(isLoading = false, isDialogVisible = false, selectedTodo = null)
                }
            }
        }
    }

    fun toggleTodoComplete(todo: Todo) {
        viewModelScope.launch {
            try {
                todoRepository.updateTodo(todo.copy(isCompleted = !todo.isCompleted))
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            try {
                todoRepository.deleteTodo(todo)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
        refreshTodos()
    }

    fun updateSortType(sortType: SortType) {
        _state.update { it.copy(sortType = sortType) }
        refreshTodos()
    }

    fun updateFilterType(filterType: FilterType) {
        _state.update { it.copy(filterType = filterType) }
        refreshTodos()
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun refreshTodos() {
        viewModelScope.launch { preferencesManager.userId.firstOrNull()?.let { loadTodos(it) } }
    }
}
