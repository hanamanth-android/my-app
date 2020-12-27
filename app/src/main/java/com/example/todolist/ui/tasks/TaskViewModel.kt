package com.example.todolist.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.todolist.data.PreferencesManager
import com.example.todolist.data.SortOrder
import com.example.todolist.data.Task
import com.example.todolist.data.TaskDao
import com.example.todolist.ui.ADD_TASK_RESULT_OK
import com.example.todolist.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TaskViewModel @ViewModelInject constructor(
    private val taskDao:TaskDao,
    private val preferencesManager: PreferencesManager,
    @Assisted private val state:SavedStateHandle
):ViewModel() {

    val searchQuery=state.getLiveData("searchQuery","")

   val preferencesFlow=preferencesManager.preferencesFlow

    private val taskEventChannel= Channel<TaskEvent>()
    val taskEvent=taskEventChannel.receiveAsFlow()

    private val tasksFlow= combine(
        searchQuery.asFlow(),
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

    fun onTaskSelected(task: Task) =viewModelScope.launch {
        taskEventChannel.send(TaskEvent.NavigateToEditTaskScreen(task))
    }

    fun onTaskSwiped(task: Task) =viewModelScope.launch {
        taskDao.delete(task)
        taskEventChannel.send(TaskEvent.ShowUndoDlelteMessage(task))
    }

    fun onUndoDeleteClick(task: Task)=viewModelScope.launch {
        taskDao.insert(task)
    }


    fun onAddNewTaskclick()=viewModelScope.launch {
        taskEventChannel.send(TaskEvent.NavigateToAddTaskScreen)
    }

    fun onAddEditResult(result:Int){
        when(result){
            ADD_TASK_RESULT_OK->showTaskSavedConfirmationMessage("Task Added")
            EDIT_TASK_RESULT_OK->showTaskSavedConfirmationMessage("Task updated")

        }
    }

    private fun showTaskSavedConfirmationMessage(text:String)=viewModelScope.launch {
        taskEventChannel.send(TaskEvent.ShowTaskSavedConfirmationMessage(text))
    }

    fun onDeleteAllCompletedClick()=viewModelScope.launch {
        taskEventChannel.send(TaskEvent.NavigateTodeleteAllCompletedScreen)
    }
    sealed class TaskEvent{
        object NavigateToAddTaskScreen :TaskEvent()
        data class NavigateToEditTaskScreen(val task: Task):TaskEvent()
        data class ShowUndoDlelteMessage(val task: Task):TaskEvent()
        data class ShowTaskSavedConfirmationMessage(val msg:String):TaskEvent()
        object NavigateTodeleteAllCompletedScreen:TaskEvent()

    }


}

