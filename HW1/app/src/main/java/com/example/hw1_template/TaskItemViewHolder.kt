package com.example.hw1_template

import android.graphics.Color
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// TaskItemViewHolder: Holds the view for each task item in the list.
// VERY IMPORTANT: This class binds individual views in the RecyclerView to your data.
class TaskItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    // TODO: Initialize your task item views, like TextView for the title, CheckBox for status, etc.
    // Initialize checkbox, title, time and delete views
    val titleTextView: TextView = itemView.findViewById(R.id.tvTaskTitle)
    val statusCheckBox: CheckBox = itemView.findViewById(R.id.cbTaskDone)
    val deleteView: ImageView = itemView.findViewById(R.id.ivTaskDelete)
    val timeView : TextView = itemView.findViewById(R.id.tvTaskTime)

    fun bind(task: TaskItem, listener: TaskItemClickListener) {
        // Set the view text to the task item values
        titleTextView.text = task.title
        timeView.text = task.time
        statusCheckBox.isChecked = (task.status == TaskStatus.COMPLETE)
        statusCheckBox.invalidate()

        // Set click listeners for task item view for edit
        itemView.setOnClickListener {
            listener.onItemEdit(task.id)
        }

        // Set click listeners for checkbox view for status change
        statusCheckBox.setOnClickListener {
            listener.onItemComplete(task.id)
            checkTaskStatus()
        }

        // Set click listeners for delete view for deletion
        deleteView.setOnClickListener {
            listener.onItemDelete(task.id)
        }

        checkTaskStatus()
    }


    private fun checkTaskStatus(){
        // Set the background color and clickable attribute of the itemView
        if (statusCheckBox.isChecked) {
            println("checked!")
            titleTextView.setTextColor(Color.GRAY) // set the text color to grey
            timeView.setTextColor(Color.GRAY) // set the text color to grey
            deleteView.setColorFilter(Color.GRAY) // Apply a grey color filter to the delete icon
            itemView.isClickable = false // make the itemView not clickable
            deleteView.isClickable = false //make the deleteView not clickable
        } else {
            println("uncheck!")
            // Set the text color to default (black)
            titleTextView.setTextColor(Color.BLACK)
            timeView.setTextColor(Color.BLACK)
            // Remove the color filter from the delete icon
            deleteView.clearColorFilter()
            itemView.isClickable = true // make the itemView clickable
            deleteView.isClickable = true //make the deleteView clickable
        }
    }

}
