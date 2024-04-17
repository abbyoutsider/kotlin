## To Do List - A task management tool

**About the Project**

This application is a task management tool that allows users to create, edit, and remove tasks. Each task requires a title, but users can optionally add a description and a deadline. It helps users stay organized by keeping track of completed and pending tasks.

**How it Works**

The main page features an "add" button in the bottom right corner. Clicking it opens a new task sheet with three fields:

* **Task Title:** Mandatory. A task cannot be created without a title.
* **Description:** Optional. Users can add details about the task.
* **Deadline:** Optional. Users can set a deadline using a time picker.

Once completed, users click "save" to add the new task to the main page list.

Tasks are displayed in a list format. New tasks are created in "pending" status with an unchecked checkbox.

Users can:

* Click on pending tasks to edit them.
* Click the delete icon to remove them.
* Click the checkbox to mark a task as complete. Completed tasks cannot be edited or deleted unless the checkbox is clicked again to mark it as pending.

If there are more than zero tasks (pending or complete), the total count will be displayed next to the app header (e.g., "To Do List (5)").

**Demo**


https://github.com/abbyoutsider/kotlin/assets/61987568/394f772d-d378-4acc-acca-0a88af87e1d5


**Development Time**

While I don't have an exact record of the hours spent, I dedicated approximately four days to complete this project.

**Challenges**

The most challenging aspect was understanding the logic and communication between different parts of the code. Since the project utilized an HW1 template with separate sections, I struggled to grasp how they interacted with each other.

Data binding, especially when implementing additional user interactions like delete, edit, and status change, proved to be another significant hurdle. Ensuring proper view updates required careful binding between user input, the task sheet view, and the task view model. Extensive debugging with print logs was necessary to achieve functionality.

**Resources**
* **Code Template:** https://github.com/sosper30/EEP523-Sp24/tree/main/HW1Template
* **General Debugging:** Gemini ([https://www.wired.com/story/how-to-use-google-gemini-ai-bard-chatbot/](https://www.wired.com/story/how-to-use-google-gemini-ai-bard-chatbot/))
* **Understanding Kotlin Drawable:** [https://developer.android.com/reference/kotlin/android/graphics/drawable/Drawable](https://developer.android.com/reference/kotlin/android/graphics/drawable/Drawable)
* **Icon Design:**
    * Material Design Icons: [https://m3.material.io/styles/icons](https://m3.material.io/styles/icons)
    * Google Fonts Icons: [https://fonts.google.com/icons](https://fonts.google.com/icons)
