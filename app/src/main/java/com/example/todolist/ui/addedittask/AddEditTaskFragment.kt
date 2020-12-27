package com.example.todolist.ui.addedittask

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.todolist.R
import com.example.todolist.databinding.FragmentAddEditTaskBinding
import com.example.todolist.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditTaskFragment:Fragment(R.layout.fragment_add_edit_task) {
    private val viewmodel:AddEditTaskViewmodel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding=FragmentAddEditTaskBinding.bind(view)

        binding.apply {
            editTextTaskName.setText(viewmodel.taskName)
            checkBoxImportant.isChecked=viewmodel.taskImportance
            checkBoxImportant.jumpDrawablesToCurrentState()
            textViewDateCreated.isVisible=viewmodel.task != null
            textViewDateCreated.text="Created : ${viewmodel.task?.createdDateFormatted}"

            editTextTaskName.addTextChangedListener{
                viewmodel.taskName=it.toString()
            }
            checkBoxImportant.setOnCheckedChangeListener{_,isChecked ->
                viewmodel.taskImportance=isChecked
            }
            fabSaveTask.setOnClickListener {
                viewmodel.onSaveTask()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewmodel.addEditTaskEvent.collect { event->
                when(event){
                    is AddEditTaskViewmodel.AddEditTaskEvent.ShowInvalidInputMessage->{
                        Snackbar.make(requireView(),event.msg,Snackbar.LENGTH_LONG).show()
                    }
                    is AddEditTaskViewmodel.AddEditTaskEvent.NavigateBackWithResult->{
                        binding.editTextTaskName.clearFocus()
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                }.exhaustive
            }
        }
    }

}