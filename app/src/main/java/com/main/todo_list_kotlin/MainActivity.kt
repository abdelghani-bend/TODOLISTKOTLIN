package com.main.todo_list_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

data class Todo(
    val id: Int,
    var title: String,
    var isCompleted: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoListTheme {
                TodoScreen()
            }
        }
    }
}

@Composable
fun TodoListTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF6200EE),
            secondary = Color(0xFF03DAC6),
            tertiary = Color(0xFF3700B3)
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen() {
    var todos by remember { mutableStateOf(emptyList<Todo>()) }
    var showDialog by remember { mutableStateOf(false) }
    var newTodoText by remember { mutableStateOf("") }
    var todoToDelete by remember { mutableStateOf<Todo?>(null) }
    var currentId by remember { mutableIntStateOf(0) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, "Add Todo")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Text(
                text = "TODO - ${todos.count { it.isCompleted }} Done / ${todos.count { !it.isCompleted }} Draft",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(16.dp)
            )
            if (todos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tasks yet. Click the + button to add one!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    items(
                        items = todos,
                        key = { it.id }
                    ) { todo ->
                        TodoItem(
                            todo = todo,
                            onDelete = { todoToDelete = todo },
                            onCheckedChange = { checked ->
                                todos = todos.map {
                                    if (it.id == todo.id) it.copy(isCompleted = checked)
                                    else it
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add New Todo") },
            text = {
                TextField(
                    value = newTodoText,
                    onValueChange = { newTodoText = it },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTodoText.isNotBlank()) {
                            todos = todos + Todo(++currentId, newTodoText)
                            newTodoText = ""
                            showDialog = false
                        }
                    }
                ) { Text("Add") }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) { Text("Cancel") }
            }
        )
    }

    todoToDelete?.let { todo ->
        AlertDialog(
            onDismissRequest = { todoToDelete = null },
            title = { Text("Delete Todo") },
            text = { Text("Are you sure you want to delete this item?") },
            confirmButton = {
                Button(
                    onClick = {
                        todos = todos.filter { it.id != todo.id }
                        todoToDelete = null
                    }
                ) { Text("Delete") }
            },
            dismissButton = {
                Button(
                    onClick = { todoToDelete = null }
                ) { Text("Cancel") }
            }
        )
    }
}


@Composable
fun TodoItem(
    todo: Todo,
    onDelete: () -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (todo.isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = todo.isCompleted,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Task Title with completion styling
                    Text(
                        text = todo.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            textDecoration = if (todo.isCompleted) {
                                TextDecoration.LineThrough
                            } else {
                                TextDecoration.None
                            }
                        ),
                        color = if (todo.isCompleted) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    Text(
                        text = if (todo.isCompleted) "Done" else "To do",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (todo.isCompleted) {
                            MaterialTheme.colorScheme.secondary
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}