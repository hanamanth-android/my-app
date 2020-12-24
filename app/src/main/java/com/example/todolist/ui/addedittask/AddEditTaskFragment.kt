package com.example.todolist.ui.addedittask

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.todolist.R
import com.example.todolist.databinding.FragmentAddEditTaskBinding
import dagger.hilt.android.AndroidEntryPoint

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

        }
    }

}