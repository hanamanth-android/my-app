package com.example.todolist.ui.deleteAllCompleted

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.room.Dao
import com.example.todolist.data.TaskDao
import com.example.todolist.di.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeleteAllCompletedViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @ApplicationScope private val applicationScope: CoroutineScope
):ViewModel() {
    fun onConfirmClick()=applicationScope.launch {
        taskDao.deleteCompletedTasks()
    }
}