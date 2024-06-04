package com.example.focusspirit.data

import com.example.focusspirit.models.FocusSession

object SampleData {
    val focusSessions: MutableList<FocusSession> = mutableListOf(
        FocusSession("User", "Leetcode", 115000L),
        FocusSession("User", "Code in Kotlin", 30000000L),
        FocusSession("User", "Exercising", 1450000L)
    )
}
