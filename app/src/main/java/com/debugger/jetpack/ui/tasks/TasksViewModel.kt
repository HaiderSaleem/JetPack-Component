package com.debugger.jetpack.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.debugger.jetpack.data.PreferencesManager
import com.debugger.jetpack.data.SortedOrder
import com.debugger.jetpack.data.Task
import com.debugger.jetpack.data.TaskDao
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val searchQuery = state.getLiveData("searchQuery", "")
    private val taskEventsChannel = Channel<TaskEvents> { }

    val taskEvents = taskEventsChannel.receiveAsFlow()

    val preferencesFlow = preferencesManager.preferencesFlow

    @FlowPreview
    private val taskFlow = combine(
        searchQuery.asFlow(),
        preferencesFlow
    ) { searchQuery, filterPreferences ->
        Pair(searchQuery, filterPreferences)
    }.flatMapLatest { (searchQuery, filterPreferences) ->
        taskDao.getTasks(
            searchQuery,
            filterPreferences.sortedOrder,
            filterPreferences.hideComplete
        )
    }

    fun onSortOrderSelected(sortedOrder: SortedOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortedOrder)
    }

    fun onHideCompletedSelected(hideComplete: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideComplete(hideComplete)
    }

    fun onTaskCompleted(task: Task) = viewModelScope.launch {
        taskEventsChannel.send(TaskEvents.NavigateToEditTaskScreen(task))
    }

    fun onTaskCheckedChange(task: Task, isChecked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = isChecked))
    }

    fun onTaskSwipe(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        taskEventsChannel.send(TaskEvents.ShowUndoDeleteMessage(task))
    }

    fun onUndoClick(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun onAddNewTaskClick() = viewModelScope.launch {
        taskEventsChannel.send(TaskEvents.NavigateToAddTaskScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            0 -> showTaskSaved("Task Added")
            1 -> showTaskSaved("Task Updated")
        }
    }

    private fun showTaskSaved(msg: String) = viewModelScope.launch {
        taskEventsChannel.send(TaskEvents.ShowSaveTaskMessage(msg))
    }

    sealed class TaskEvents {
        object NavigateToAddTaskScreen : TaskEvents()
        data class NavigateToEditTaskScreen(val task: Task) : TaskEvents()
        data class ShowUndoDeleteMessage(val task: Task) : TaskEvents()
        data class ShowSaveTaskMessage(val msg: String) : TaskEvents()
    }

    @FlowPreview
    val task = taskFlow.asLiveData()

}