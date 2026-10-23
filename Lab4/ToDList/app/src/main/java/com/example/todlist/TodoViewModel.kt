package com.example.todlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todlist.data.AppDatabase
import com.example.todlist.data.ListSummary
import com.example.todlist.data.TaskItem
import com.example.todlist.data.TodoList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TodoViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AppDatabase.get(app).todoDao()

    /** Toate listele, cu numărul de task-uri. */
    val lists: StateFlow<List<ListSummary>> = dao.observeLists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Lista deschisă acum (null = suntem pe ecranul cu liste). */
    private val _openListId = MutableStateFlow<Long?>(null)
    val openListId: StateFlow<Long?> = _openListId.asStateFlow()

    val openList: StateFlow<TodoList?> = _openListId
        .flatMapLatest { id -> if (id == null) flowOf(null) else dao.observeList(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val tasks: StateFlow<List<TaskItem>> = _openListId
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else dao.observeTasks(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ---------- Navigare ----------

    fun openList(id: Long) {
        _openListId.value = id
    }

    fun closeList() {
        _openListId.value = null
    }

    // ---------- CRUD liste ----------

    fun addList(name: String) = viewModelScope.launch {
        dao.insertList(TodoList(name = name.trim()))
    }

    fun renameList(id: Long, name: String) = viewModelScope.launch {
        dao.renameList(id, name.trim())
    }

    fun deleteList(id: Long) = viewModelScope.launch {
        if (_openListId.value == id) closeList()
        dao.deleteList(id)
    }

    // ---------- CRUD task-uri ----------

    fun addTask(title: String, description: String) {
        val listId = _openListId.value ?: return
        viewModelScope.launch {
            dao.insertTask(
                TaskItem(listId = listId, title = title.trim(), description = description.trim())
            )
        }
    }

    fun editTask(task: TaskItem, title: String, description: String) = viewModelScope.launch {
        dao.updateTask(task.copy(title = title.trim(), description = description.trim()))
    }

    fun toggleDone(task: TaskItem) = viewModelScope.launch {
        dao.updateTask(task.copy(done = !task.done))
    }

    fun deleteTask(task: TaskItem) = viewModelScope.launch {
        dao.deleteTask(task)
    }
}
