package com.debugger.jetpack.ui.addedittask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debugger.jetpack.data.Task
import com.debugger.jetpack.data.TaskDao
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditTaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val task = state.get<Task>("task")
    private val addEditTaskEvent = Channel<AddEditTaskEvent> { }
    private val addEdItTaskEventFlow = addEditTaskEvent.receiveAsFlow()

    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            state.set("taskName", value)
        }

    var taskImp = state.get<Boolean>("taskImp") ?: task?.important ?: false
        set(value) {
            field = value
            state.set("taskImp", value)
        }

    fun onSaveClick() {
        if (taskName.isBlank()) {
            showInvalidInputMessage("Name cannot by empty")
        }

        if (task != null) {
            val updatedTask = task.copy(name = taskName, important = taskImp)
            updateTask(updatedTask)
        } else {
            val newTask = Task(name = taskName, important = taskImp)
            createTask(newTask)
        }

    }

    private fun showInvalidInputMessage(msg: String) = viewModelScope.launch {
        addEditTaskEvent.send(AddEditTaskEvent.ShowInvalidInputMessage(msg))
    }

    private fun createTask(newTask: Task) = viewModelScope.launch {
        taskDao.insert(newTask)
        addEditTaskEvent.send(AddEditTaskEvent.NavigateBackResult(0))
    }

    private fun updateTask(updatedTask: Task) = viewModelScope.launch {
        taskDao.update(updatedTask)
        addEditTaskEvent.send(AddEditTaskEvent.NavigateBackResult(1))
    }

    sealed class AddEditTaskEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditTaskEvent()
        data class NavigateBackResult(val result: Int) : AddEditTaskEvent()

    }
}