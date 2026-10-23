package com.example.todlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todlist.ui.ListsScreen
import com.example.todlist.ui.TasksScreen
import com.example.todlist.ui.theme.ToDListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDListTheme {
                TodoApp()
            }
        }
    }
}

/** Arată ecranul cu liste sau ecranul cu task-urile listei deschise. */
@Composable
fun TodoApp(vm: TodoViewModel = viewModel()) {
    val openListId by vm.openListId.collectAsStateWithLifecycle()

    if (openListId == null) {
        val lists by vm.lists.collectAsStateWithLifecycle()
        ListsScreen(
            lists = lists,
            onOpen = vm::openList,
            onAdd = { vm.addList(it) },
            onRename = { id, name -> vm.renameList(id, name) },
            onDelete = { vm.deleteList(it) },
        )
    } else {
        val openList by vm.openList.collectAsStateWithLifecycle()
        val tasks by vm.tasks.collectAsStateWithLifecycle()

        // Butonul "Back" al telefonului ne duce înapoi la liste.
        BackHandler(onBack = vm::closeList)

        TasksScreen(
            listName = openList?.name.orEmpty(),
            tasks = tasks,
            onBack = vm::closeList,
            onAdd = { title, desc -> vm.addTask(title, desc) },
            onEdit = { task, title, desc -> vm.editTask(task, title, desc) },
            onToggle = { vm.toggleDone(it) },
            onDelete = { vm.deleteTask(it) },
        )
    }
}
