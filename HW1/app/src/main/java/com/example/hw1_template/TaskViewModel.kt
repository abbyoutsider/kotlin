package com.example.hw1_template

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// TaskViewModel: Handles the business logic of your task data, separating concerns from the UI.
// VITAL: This ViewModel should be the sole source of truth for your UI regarding task data.
class TaskViewModel : ViewModel() {
    // TODO: Implement the logic for managing task data, such as adding, deleting, and updating tasks.
    // Private mutable list of tasks
    private val _tasks = MutableLiveData<List<TaskItem>>()

    // Public immutable list of tasks to be observed by the UI
    val tasks: LiveData<List<TaskItem>> = _tasks

    init {
        // Initialize with an empty list
        _tasks.value = emptyList()
    }

    // Adds a new task to the list
    fun addTask(newTask: TaskItem) {
        val updatedTasks = _tasks.value?.toMutableList() ?: mutableListOf()
        updatedTasks.add(newTask)
        _tasks.value = updatedTasks
    }

    // Deletes a task from the list
    fun deleteTask(taskId: Int) {
        _tasks.value = _tasks.value?.filterNot { it.id == taskId }
    }

    // Updates an existing task
    fun updateTask(updatedTask: TaskItem) {
        _tasks.value = _tasks.value?.map { if (it.id == updatedTask.id) updatedTask else it }
    }

    // Get the current list of tasks
    fun getTasks(): List<TaskItem> {
        return tasks.value.orEmpty()
    }

    // Get a task item by its task id
    fun getTaskById(taskId: Int): TaskItem? {
        return tasks.value?.find { it.id == taskId }
    }
}
