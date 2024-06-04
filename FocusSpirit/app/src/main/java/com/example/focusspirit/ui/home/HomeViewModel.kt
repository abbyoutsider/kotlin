package com.example.focusspirit.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    // Declare a mutable live data object with an initial value of "Skills Learned: 0"
    private val _text = MutableLiveData<String>().apply {
        value = "Skills Learned: 0"
    }

    // Expose an immutable LiveData object for observing changes in the value of _text
    val level: LiveData<String> = _text

    // Define a function to update the value of _text, which represents the number of skills learned
    fun updateLevel(level: Int) {
        _text.value = "Skills Learned: $level"
    }

}