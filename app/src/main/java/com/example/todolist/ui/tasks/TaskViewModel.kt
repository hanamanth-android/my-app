package com.example.todolist.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.PreferencesManager
import com.example.todolist.data.SortOrder
import com.example.todolist.data.Task
import com.example.todolist.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class TaskViewModel @ViewModelInject constructor(
    private val taskDao:TaskDao,
    private val preferencesManager: PreferencesManager
):ViewModel() {

    val searchQuery=MutableStateFlow("")

   val preferencesFlow=preferencesManager.preferencesFlow

    private val tasksFlow= combine(
        searchQuery,
       preferencesFlow
    ){query,filterPreferences->
        Pair(query,filterPreferences)
    }.flatMapLatest {(query,filterPreferences)->
        taskDao.getTasks(query,filterPreferences.sortOrder,filterPreferences.hideCompleted)
    }

    val tasks=tasksFlow.asLiveData()

    fun onSortorderSelected(sortOrder: SortOrder)=viewModelScope.launch{
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted:Boolean)=viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) =viewModelScope.launch {
        taskDao.update(task.copy(completed =isChecked ))
    }

    fun onTaskSelected(task: Task) {

    }

    fun onTaskSwiped(task: Task) =viewModelScope.launch {
        taskDao.delete(task)
    }


}

