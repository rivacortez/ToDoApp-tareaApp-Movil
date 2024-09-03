package pe.edu.upc.todoapp.ui.viewmodel
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel


class TaskViewModel : ViewModel() {
    private val _tasks = mutableStateListOf<String>()
    val tasks: SnapshotStateList<String> get() = _tasks

    fun addTask(task: String) {
        _tasks.add(task)
    }

    fun updateTask(index: Int, newTask: String) {
        if (index in _tasks.indices) {
            _tasks[index] = newTask
        }
    }

    fun deleteTask(index: Int) {
        if (index in _tasks.indices) {
            _tasks.removeAt(index)
        }
    }
}