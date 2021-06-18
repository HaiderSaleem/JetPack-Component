package com.debugger.jetpack.ui.deleteallcomplete

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.debugger.jetpack.data.TaskDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeleteAllViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val appScope: CoroutineScope
) : ViewModel() {

    fun onConfirmClick() = appScope.launch {
        taskDao.deleteCompletedTasks()
    }

}