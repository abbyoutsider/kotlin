package com.example.hw1_template


// TaskItemClickListener: An interface for handling clicks on items in your list.
// ESSENTIAL: You must implement this interface in your ViewHolder or Activity to respond to user interactions.
interface TaskItemClickListener {
    fun onItemComplete(taskId: Int)
    // Add more methods as needed, like onItemDelete, onItemEdit, etc.
    fun onItemDelete(taskId: Int)
    fun onItemEdit(taskId: Int)
}
