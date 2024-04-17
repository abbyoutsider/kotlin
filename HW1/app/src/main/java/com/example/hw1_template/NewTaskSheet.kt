package com.example.hw1_template

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// NewTaskSheet: This Activity/Fragment is responsible for adding new tasks.
// Neglecting the input handling here will break the core functionality of task addition.
class NewTaskSheet : AppCompatActivity() {

    // Lateinit is used for variables that will be initialized later.
    private lateinit var titleView: EditText
    private lateinit var descriptionView: EditText
    private lateinit var saveButton: Button
    private lateinit var btnOpenTimePicker: Button
    private lateinit var taskViewModel: TaskViewModel

    // onCreate: Again, crucial for setting up the layout and functionality.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_new_task_sheet)

        // Set up the UI components like EditText and Button, and handle their events.
        // TODO: Initialize your EditText and Button for task input and saving
        // Initialize the views and Buttons from the layout
        titleView = findViewById(R.id.etTaskTitle)
        descriptionView = findViewById(R.id.etTaskDescription)
        saveButton = findViewById(R.id.btnSaveTask)
        btnOpenTimePicker = findViewById<Button>(R.id.btnOpenTimePicker)
        taskViewModel = TaskViewModel()

        // Initialize the default value for selected task time
        var selectedTime = ""
        // Initialize the editTask from intent if it is provided
        var editTask = intent.getSerializableExtra("edit_task") as? TaskItem
        if (editTask != null) {
            // We're editing an existing task so populate the task sheet with existing data
            println("Editing task: "+editTask.id)
            titleView.setText(editTask.title)
            descriptionView.setText(editTask.description)
            // Set the timePicker to existing time
            if(editTask.time !=""){
                val timeParts = editTask.time.split(":")
                val hour = timeParts[0].toInt()
                val minute = timeParts[1].toInt()
                selectedTime = String.format("%02d:%02d", hour, minute)
                btnOpenTimePicker.text = selectedTime
            }
        }
        //Set an onClickListener for the "SELECT TIME" button to pick a time deadline for task
        btnOpenTimePicker.setOnClickListener {
            val timePickerDialog = TimePickerDialog(this,
                { _, hourOfDay, minute ->
                    // Get selectedTime as a string representing the selected time
                    selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                    // Set selectedTime as the text of the timePicker button
                    btnOpenTimePicker.text = selectedTime
                },
                12, 0, false)
            timePickerDialog.show()
        }

        // Set an onClickListener for the "SAVE" button to save a new task item
        saveButton.setOnClickListener {
            // Get the title the user entered
            val title = titleView.text.toString()
            // Get the description the user entered
            val description = descriptionView.text.toString()
            var time = btnOpenTimePicker.text.toString()
            // Check if time input is valid, valid length should be 5, hh:mm
            if(time.length!=5) time= ""
            // Check if title has been filled
            if (title.isNotEmpty()) {
                // Generate a unique ID
                if(editTask == null){
                    // Build a new task item and send it to viewModel
                    var newTaskId = TaskIdGenerator.getNextId();
                    // Create a new TaskItem object with the retrieved info
                    var newTask = TaskItem(newTaskId, title, description, time)
                    // Call the ViewModel to handle adding the new task
                    val resultIntent = Intent().apply {
                        putExtra("new_task", newTask)
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                }else{
                    // Update existing task item and send it to viewModel
                    editTask.title = title
                    editTask.description = description
                    editTask.time = time
                    val resultIntent = Intent().apply {
                        putExtra("edit_task", editTask)
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                }
                // Close the NewTaskSheet
                finish() // Closes the activity
            } else {
                // Show an error message if the input is not valid
                Toast.makeText(this, "Please enter a task title", Toast.LENGTH_SHORT).show()
            }
        }
    }

}

object TaskIdGenerator {
    private var taskId = 0

    fun getNextId(): Int {
        return ++taskId
    }
}