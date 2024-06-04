package com.example.focusspirit.ui.statistics
// FocusSessionAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.focusspirit.R
import com.example.focusspirit.models.FocusSession

class FocusSessionAdapter(private val focusSessions: List<FocusSession>) :
    RecyclerView.Adapter<FocusSessionAdapter.FocusSessionViewHolder>() {

        class FocusSessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
            val durationTextView: TextView = itemView.findViewById(R.id.durationTextView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FocusSessionViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_focus_session, parent, false)
            return FocusSessionViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: FocusSessionViewHolder, position: Int) {
            val currentSession = focusSessions[position]
            holder.titleTextView.text = currentSession.title
            holder.durationTextView.text = reformatDuration(currentSession.focusedDuration)
        }

    fun reformatDuration(focusDuration: Long): String {
        val seconds = (focusDuration / 1000) % 60
        val minutes = (focusDuration / (1000 * 60)) % 60
        val hours = focusDuration / (1000 * 60 * 60)

        return buildString {
            if (hours > 0) {
                append("$hours hours")
            }
            if (minutes > 0) {
                if (hours > 0) append(", ")
                append("$minutes minutes")
            }
            if (seconds > 0 || (hours == 0L && minutes == 0L)) {
                if (hours > 0 || minutes > 0) append(", ")
                append("$seconds seconds")
            }
        }
    }

    override fun getItemCount() = focusSessions.size
}
