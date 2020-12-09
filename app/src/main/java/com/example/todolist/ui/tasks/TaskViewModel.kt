package com.example.todolist.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.todolist.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class TaskViewModel @ViewModelInject constructor(
    private val taskDao:TaskDao
):ViewModel() {

    val searchQuery=MutableStateFlow("")

    private val tasksFlow=searchQuery.flatMapLatest {
        taskDao.getTasks(it)
    }

    val tasks=tasksFlow.asLiveData()

}