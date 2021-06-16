package com.debugger.jetpack.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.debugger.jetpack.data.TaskDao
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    @FlowPreview
    private val taskFlow = searchQuery.flatMapLatest {
        taskDao.getTasks(it)
    }

    @FlowPreview
    val task = taskFlow.asLiveData()

}