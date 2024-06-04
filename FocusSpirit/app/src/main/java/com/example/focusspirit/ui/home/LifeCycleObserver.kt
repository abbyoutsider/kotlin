package com.example.focusspirit.ui.home

import android.os.SystemClock
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class FocusSessionManager(private val onSessionPause: () -> Unit, private val onSessionResume: () -> Unit) : LifecycleObserver {

    var focusStartTime: Long = 0
    private var focusedTime: Long = 0
    var isPaused: Boolean = false

    // Starts a focus session - resets start time and pause flag
    fun startFocusSession() {
        focusStartTime = SystemClock.elapsedRealtime()
        isPaused = false
    }

    // Stops a focus session - calculates duration, resets timers and flags
    fun stopFocusSession(): Long {
        var focusDurationTime = 0L
        if (isPaused) {
            // Use accumulated paused time if session was paused
            focusDurationTime = focusedTime
        } else {
            val focusEndTime = SystemClock.elapsedRealtime()
            // Calculate total duration including elapsed time since start and any paused time
            focusDurationTime = focusEndTime - focusStartTime + focusedTime
        }
        focusedTime = 0L  // Reset accumulated paused time
        focusStartTime = 0L  // Reset start time
        return focusDurationTime
    }

    // Pauses a focus session - accumulates elapsed time and sets pause flag
    fun pauseFocusSession() {
        focusedTime += SystemClock.elapsedRealtime() - focusStartTime
        isPaused = true
        onSessionPause()  // Call optional callback for session pause event (assuming defined elsewhere)
    }

    // Resumes a focus session - resets start time and clears pause flag
    fun resumeFocusSession() {
        focusStartTime = SystemClock.elapsedRealtime()
        isPaused = false
        onSessionResume()  // Call optional callback for session resume event (assuming defined elsewhere)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        if (focusStartTime != 0L) {
            pauseFocusSession()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        // Do nothing here as we will resume on button click
    }
}
