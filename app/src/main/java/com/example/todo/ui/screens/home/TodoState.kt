package com.example.todo.ui.screens.home
import com.example.todo.data.model.Todo

data class TodoState(
    val todos: List<Todo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTodo: Todo? = null,
    val isDialogVisible: Boolean = false,
    val sortType: SortType = SortType.DATE_DESC,
    val filterType: FilterType = FilterType.ALL,
    val searchQuery: String = ""
)
