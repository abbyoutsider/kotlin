package com.example.hw1_template

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

// TaskItemAdapter: Manages how task items are displayed and interacted with in a RecyclerView.
// CRITICAL: This adapter links your TaskItem data to the RecyclerView in the UI.
class TaskItemAdapter(var tasks: List<TaskItem>, private val listener: MainActivity) : RecyclerView.Adapter<TaskItemViewHolder>() {

    // onCreateViewHolder: Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item_cell, parent, false)
        return TaskItemViewHolder(view)
    }

    // onBindViewHolder: Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: TaskItemViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task, listener)
    }

    // getItemCount: Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = tasks.size

}
