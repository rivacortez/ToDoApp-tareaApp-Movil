package pe.edu.upc.todoapp.ui.tasklist
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
@Composable
fun TaskList(
    tasks: List<String> = emptyList(),
    onSelectTask: (Int) -> Unit = {},
    onAddTask: () -> Unit = {},
    onDeleteTask: (Int) -> Unit = {}
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddTask() }) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            itemsIndexed(tasks, key = { index, _ -> index }) { index, task ->
                DraggableTask(
                    task = task,
                    onSelectTask = { onSelectTask(index) },
                    onDeleteTask = {
                        onDeleteTask(index)
                    }
                )
            }
        }
    }
}



@Composable
private fun DraggableTask(
    task: String,
    onSelectTask: () -> Unit,
    onDeleteTask: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    val limit = -200f
    val isDeleted = remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(offsetX) {
        if (offsetX < limit && !isDeleted.value) {
            showDeleteDialog = true
            offsetX = 0f
        }
    }


    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Está seguro de que desea eliminar esta tarea?") },
            confirmButton = {
                TextButton(onClick = {
                    isDeleted.value = true
                    showDeleteDialog = false
                    onDeleteTask()
                }) {
                    Text("Continuar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                }) {
                    Text("Cancelar")
                }
            }
        )
    }


    if (!isDeleted.value) {
        Card(
            onClick = onSelectTask,
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .fillMaxWidth()
                .padding(4.dp)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        offsetX += delta
                    },
                    onDragStopped = {
                        if (offsetX < limit) {
                            offsetX = limit
                        } else {
                            offsetX = 0f
                        }
                    }
                )
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { onSelectTask() })
                }
        ) {
            Text(
                text = task,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TaskListPreview() {
    TaskList()
}