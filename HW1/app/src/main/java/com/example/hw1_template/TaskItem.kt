package com.example.hw1_template

import java.io.Serializable

// TaskItem: Represents the data structure for a task in your to-do list.
// IMPORTANT: This class must be well-defined; it's the blueprint for your task data.
// Add more fields as necessary, like dueDate, priority, etc.
data class TaskItem(
    val id: Int,
    var title: String,
    var description: String,
    var time: String,
    var status: TaskStatus = TaskStatus.PENDING
) : Serializable

enum class TaskStatus {
    PENDING,
    COMPLETE
}