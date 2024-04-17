package com.example.hw1_template

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

// MainActivity: The heart of your application's UI.
// This class should coordinate the main user interactions and screen transitions.
class MainActivity : AppCompatActivity(),  TaskItemClickListener {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskItemAdapter
    private lateinit var tasks: List<TaskItem>
    private lateinit var taskCountView: TextView
    private val NEW_TASK_REQUEST_CODE = 1001
    private val EDIT_TASK_REQUEST_CODE = 1002




    // onCreate: Critical for initializing the activity and setting up the UI components.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the TaskViewModel
        taskViewModel = TaskViewModel()
        // Get the tasks from taskViewModel
        tasks = taskViewModel.getTasks()
        // Initialize the view for task count
        taskCountView = findViewById(R.id.tvTaskCount)

        // RecyclerView setup: Essential for displaying a list of items.
        // You MUST properly initialize and configure your RecyclerView and its adapter.
        recyclerView = findViewById<RecyclerView>(R.id.rvTasks)
        recyclerView.layoutManager = LinearLayoutManager(this) // Set the layout manager
        // TaskItemAdapter is your custom adapter for the RecyclerView. You need to create this file.
        // TODO: Initialize your TaskItemAdapter with data
        // Initialize taskAdapter with list of task items
        taskAdapter = TaskItemAdapter(tasks, this)
        recyclerView.adapter = taskAdapter

        // FloatingActionButton: Triggers the creation of new tasks.
        // The onClickListener here is vital for handling user actions to add new tasks.
        val fabNewTask = findViewById<FloatingActionButton>(R.id.fabNewTask)
        fabNewTask.setOnClickListener {
            // TODO: Implement the logic to show NewTaskSheet (you need to create this Activity/Fragment)
            // Start NewTaskSheet as an Activity
            // Initialize NewTaskSheet as an Activity
            val taskSheetIntent =  Intent(this, NewTaskSheet::class.java)
            startActivityForResult(taskSheetIntent, NEW_TASK_REQUEST_CODE)
        }

        taskViewModel.tasks.observe(this) { tasks ->
            // Update your adapter with the new tasks
            taskAdapter.tasks = tasks
            taskAdapter.notifyDataSetChanged()
            // Update task count on the main page
            if (tasks.size > 0) {
                taskCountView.text = String.format("(%d)", tasks.size)
            } else {
                taskCountView.text = ""
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == NEW_TASK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Get a new TaskItem from the result data
            val newTask = data?.getSerializableExtra("new_task") as? TaskItem
            if (newTask != null) {
                // Add the new task to the TaskViewModel
                taskViewModel.addTask(newTask)
                println("New Task Added: "+newTask.id)
            }
        }

        if (requestCode == EDIT_TASK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Get an edited TaskItem from the result data
            val editTask = data?.getSerializableExtra("edit_task") as? TaskItem
            if (editTask != null) {
                // Update the edited task to the TaskViewModel
                taskViewModel.updateTask(editTask)
                println("Task Updated: "+editTask.id)
            }
        }
    }

    // Implement TaskItemClickListener interface
    // Update the task status when clicking status checkbox
    override fun onItemComplete(taskId: Int) {
        var task = taskViewModel.getTaskById(taskId)
        if (task != null) {
            if(task.status==TaskStatus.COMPLETE){
                task.status =TaskStatus.PENDING
            }else{
                task.status =TaskStatus.COMPLETE
            }
        }
    }

    // Remove the task from the list when clicking delete icon
    override fun onItemDelete(taskId: Int) {
        taskViewModel.deleteTask(taskId)
        // Show an info message when a task has been removed
        Toast.makeText(this, "Your task has been removed", Toast.LENGTH_SHORT).show()
    }

    // Open the task sheet for editing when clicking on the task item
    override fun onItemEdit(taskId: Int) {
        var taskSheetIntent =  Intent(this, NewTaskSheet::class.java)
        // Send the clicked task item for populating data
        val intent = taskSheetIntent.apply {
            putExtra("edit_task", taskViewModel.getTaskById(taskId))
        }
        startActivityForResult(intent, EDIT_TASK_REQUEST_CODE)
    }

    // Instructions:
    // 1. Create NewTaskSheet.kt for handling the creation of new tasks.
    // 2. Implement TaskItemAdapter.kt to manage how task data is bound to the RecyclerView.
    // 3. Understand how TaskItem.kt represents individual task data.
    // 4. TaskViewModel.kt should handle all your data logic, like adding and retrieving tasks.
    // 5. TaskItemClickListener.kt and TaskItemViewHolder.kt are crucial for handling item interactions in your RecyclerView.

    // To add new Kotlin files for your classes, like NewTaskSheet or TaskItemAdapter,
// right-click on the package directory in the 'src/main/java' (or 'src/main/kotlin') folder in the Project view,
// then choose 'New' > 'Kotlin File/Class', name your file/class

}


