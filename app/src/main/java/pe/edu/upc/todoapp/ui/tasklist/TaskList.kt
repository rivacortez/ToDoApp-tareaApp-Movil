package pe.edu.upc.todoapp.ui.tasklist
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun TaskList(
    tasks: List<String> = emptyList(),
    onSelectTask: (Int) -> Unit = {},
    onAddTask: () -> Unit = {},
    onDeleteTask: (Int) -> Unit = {}
) {
    val offsets = remember { mutableStateListOf<Animatable<Float, AnimationVector1D>>() }


    LaunchedEffect(tasks.size) {

        if (tasks.size > offsets.size) {
            offsets.addAll(List(tasks.size - offsets.size) { Animatable(0f) })
        }

        if (tasks.size < offsets.size) {
            offsets.removeRange(tasks.size, offsets.size)
        }
    }


    val scope = rememberCoroutineScope()
    val taskBeingDeleted = remember { mutableStateOf(-1) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {

                onAddTask()

            },
                ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            itemsIndexed(tasks, key = { index, _ -> index }) { index, task ->
                val offsetX = offsets.getOrNull(index) ?: remember { Animatable(0f) }

                DraggableTask(
                    task = task,
                    offsetX = offsetX,
                    onSelectTask = { onSelectTask(index) },
                    onDeleteTask = {
                        taskBeingDeleted.value = index
                        scope.launch {

                            offsetX.animateTo(-200f)
                            onDeleteTask(index)

                            if (index < offsets.size) {
                                offsets[index].animateTo(0f)
                            }
                        }
                    }
                )
            }
        }
    }


    LaunchedEffect(taskBeingDeleted.value) {
        if (taskBeingDeleted.value != -1 && taskBeingDeleted.value < offsets.size) {
            offsets[taskBeingDeleted.value].animateTo(0f)
            taskBeingDeleted.value = -1
        }
    }
}

@Composable
private fun DraggableTask(
    task: String,
    offsetX: Animatable<Float, AnimationVector1D>,
    onSelectTask: () -> Unit,
    onDeleteTask: () -> Unit
) {
    val limit = -200f
    var showDeleteDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()


    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Segurisimo?") },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        offsetX.animateTo(limit, tween(durationMillis = 100))
                        onDeleteTask()

                        showDeleteDialog = false
                    }
                    showDeleteDialog = false
                }) {
                    Text("Continuar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        offsetX.animateTo(0f)
                    }
                    showDeleteDialog = false
                }) {
                    Text("Cancelar")
                }
            }
        )
    }


    LaunchedEffect(key1 = offsetX.value) {
        if (offsetX.value < limit) {

            showDeleteDialog = true


        }
    }

    Card(
        onClick = onSelectTask,
        modifier = Modifier
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .fillMaxWidth()
            .padding(4.dp)
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    coroutineScope.launch {
                        offsetX.snapTo(offsetX.value + delta)
                    }
                },
                onDragStopped = {
                    coroutineScope.launch {
                        if (offsetX.value < limit) {
                            showDeleteDialog = true
                        } else {
                            offsetX.animateTo(0f)
                        }
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

@Preview(showBackground = true)
@Composable
fun TaskListPreview() {
    TaskList()
}


