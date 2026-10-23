package com.example.todlist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.todlist.data.ListSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen(
    lists: List<ListSummary>,
    onOpen: (Long) -> Unit,
    onAdd: (String) -> Unit,
    onRename: (Long, String) -> Unit,
    onDelete: (Long) -> Unit,
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var listToRename by remember { mutableStateOf<ListSummary?>(null) }
    var listToDelete by remember { mutableStateOf<ListSummary?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Listele mele") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Listă nouă")
            }
        },
    ) { padding ->
        if (lists.isEmpty()) {
            EmptyState("Nu ai nicio listă.\nApasă + ca să creezi una.", Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(lists, key = { it.id }) { list ->
                    ListCard(
                        list = list,
                        onClick = { onOpen(list.id) },
                        onRename = { listToRename = list },
                        onDelete = { listToDelete = list },
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        ListNameDialog(
            title = "Listă nouă",
            onDismiss = { showAddDialog = false },
            onConfirm = { onAdd(it); showAddDialog = false },
        )
    }

    listToRename?.let { list ->
        ListNameDialog(
            title = "Redenumește lista",
            initialName = list.name,
            onDismiss = { listToRename = null },
            onConfirm = { onRename(list.id, it); listToRename = null },
        )
    }

    listToDelete?.let { list ->
        ConfirmDeleteDialog(
            message = "Lista „${list.name}” și toate task-urile ei vor fi șterse.",
            onDismiss = { listToDelete = null },
            onConfirm = { onDelete(list.id); listToDelete = null },
        )
    }
}

@Composable
private fun ListCard(
    list: ListSummary,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(list.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${list.done}/${list.total} finalizate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onRename) {
                Icon(Icons.Default.Edit, contentDescription = "Redenumește")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Șterge")
            }
        }
    }
}

@Composable
fun EmptyState(text: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
