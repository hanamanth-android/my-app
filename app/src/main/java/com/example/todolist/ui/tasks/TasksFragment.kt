package com.example.todolist.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.data.SortOrder
import com.example.todolist.data.Task
import com.example.todolist.databinding.FragmentTasksBinding
import com.example.todolist.util.exhaustive
import com.example.todolist.util.onQueryTextchanged
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksFragment:Fragment(R.layout.fragment_tasks),TasksAdapter.OnItemClickLister {
    private val viewModel:TaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding=FragmentTasksBinding.bind(view)

        val tasksAdapter=TasksAdapter(this)

        binding.apply {
            recylerviewTasks.apply {
                adapter=tasksAdapter
                layoutManager =LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            ItemTouchHelper(object :ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.RIGHT){
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task=tasksAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTaskSwiped(task)
                }
            }).attachToRecyclerView(recylerviewTasks)

            fabAddTask.setOnClickListener {
                viewModel.onAddNewTaskclick()
            }
        }

        setFragmentResultListener("add_edit_request"){_,bundle->
            val result=bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }
        viewModel.tasks.observe(viewLifecycleOwner){
            tasksAdapter.submitList(it)
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.taskEvent.collect {event->
                when(event){
                    is TaskViewModel.TaskEvent.ShowUndoDlelteMessage->{
                     Snackbar.make(requireView(),"Task deleted",Snackbar.LENGTH_LONG)
                         .setAction("UNDO"){
                             viewModel.onUndoDeleteClick(event.task)
                         }.show()
                 }
                    is TaskViewModel.TaskEvent.NavigateToAddTaskScreen -> {
                        val action=TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(null,"New Task")
                        findNavController().navigate(action)
                    }
                    is TaskViewModel.TaskEvent.NavigateToEditTaskScreen -> {
                        val action=TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(task = event.task,"Edit Task")
                        findNavController().navigate(action)
                    }
                    is TaskViewModel.TaskEvent.ShowTaskSavedConfirmationMessage -> {
                        Snackbar.make(requireView(),event.msg,Snackbar.LENGTH_SHORT).show()
                    }
                }.exhaustive
            }
        }
        setHasOptionsMenu(true)
    }

    override fun onItemclick(task: Task) {
        viewModel.onTaskSelected(task)
    }

    override fun onCheckBoxclick(task: Task, isChecked: Boolean) {
        viewModel.onTaskCheckedChanged(task,isChecked)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_tasks,menu)

        val searchItem=menu.findItem(R.id.action_search)
        val searchView=searchItem.actionView as SearchView

        searchView.onQueryTextchanged {
            //update search query
            viewModel.searchQuery.value=it
        }
        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.actin_hide_complted).isChecked = viewModel.preferencesFlow.first().hideCompleted

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
      return  when(item.itemId){
            R.id.action_sort_by_name->{
                viewModel.onSortorderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.action_sort_by_date_created->{
              viewModel.onSortorderSelected(SortOrder.BY_DATE)
                true
            }
            R.id.actin_hide_complted->{
                item.isChecked=!item.isChecked
                viewModel.onHideCompletedClick(item.isChecked)
                true
            }
            R.id.action_delete_all_complted->{

                true
            }
            else->super.onOptionsItemSelected(item)
        }
    }
}