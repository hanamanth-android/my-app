package com.example.todolist.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.data.Task
import com.example.todolist.databinding.ItemTaskBinding

class TasksAdapter(private val listener:OnItemClickLister):ListAdapter<Task,TasksAdapter.TasksViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val binding=ItemTaskBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TasksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val currentItem=getItem(position)
        holder.bind(currentItem)
    }

  inner  class TasksViewHolder(private val binding:ItemTaskBinding):RecyclerView.ViewHolder(binding.root){

        init {
            binding.apply {
                root.setOnClickListener {
                    val position=adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val task=getItem(position)
                        listener.onItemclick(task)
                    }
                }
                checkboxComplted.setOnClickListener {
                    val position=adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val task=getItem(position)
                        listener.onCheckBoxclick(task,checkboxComplted.isChecked)
                    }
                }
            }
        }

        fun bind(task:Task){
            binding.apply {
                checkboxComplted.isChecked=task.completed
                textViewName.text=task.name
                textViewName.paint.isStrikeThruText=task.completed
                labelPriority.isVisible=task.important
            }
        }
    }

    interface OnItemClickLister{
        fun onItemclick(task: Task)
        fun onCheckBoxclick(task: Task,isChecked:Boolean)
    }
    class DiffCallback:DiffUtil.ItemCallback<Task>(){
        override fun areItemsTheSame(oldItem: Task, newItem: Task)=oldItem.id==newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task)= oldItem==newItem

    }
}