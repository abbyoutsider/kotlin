package com.example.focusspirit.models

import java.io.Serializable


data class FocusSession(val userName: String, val title: String, val focusedDuration: Long) :
    Serializable
