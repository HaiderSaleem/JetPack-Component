package com.debugger.jetpack.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
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
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    private val taskEventsChannel = Channel<TaskEvents> { }

    val taskEvents = taskEventsChannel.receiveAsFlow()

    val preferencesFlow = preferencesManager.preferencesFlow

    @FlowPreview
    private val taskFlow = combine(
        searchQuery,
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
        taskDao.update(task)
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

    sealed class TaskEvents {
        data class ShowUndoDeleteMessage(val task: Task) : TaskEvents()
    }

    @FlowPreview
    val task = taskFlow.asLiveData()

}