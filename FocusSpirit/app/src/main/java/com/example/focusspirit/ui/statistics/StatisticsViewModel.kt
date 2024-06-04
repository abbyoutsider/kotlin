package com.example.focusspirit.ui.statistics

import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.focusspirit.models.FocusSession
import java.io.File
import java.io.ObjectInputStream
import android.content.Context
import com.example.focusspirit.data.SampleData

class StatisticsViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is statistics Fragment"
    }
    val text: LiveData<String> = _text
}