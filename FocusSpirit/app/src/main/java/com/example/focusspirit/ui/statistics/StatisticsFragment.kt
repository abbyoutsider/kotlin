package com.example.focusspirit.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.focusspirit.data.SampleData
import com.example.focusspirit.databinding.FragmentStatisticsBinding
import com.example.focusspirit.models.FocusSession
import java.io.File
import java.io.ObjectInputStream

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val FILE_NAME = "focus_sessions.dat"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var focusSessions: MutableList<FocusSession>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FocusSessionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Load the focus sessions
        loadFocusSessions()

        // Set up the adapter
        adapter = FocusSessionAdapter(focusSessions)
        recyclerView.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadFocusSessions() {
        val file = File(requireContext().filesDir, FILE_NAME)
        if (file.exists()) {
            ObjectInputStream(file.inputStream()).use {
                @Suppress("UNCHECKED_CAST")
                focusSessions = it.readObject() as MutableList<FocusSession>
            }
        } else {
            // Load sample data
            focusSessions = SampleData.focusSessions.toMutableList()
        }
    }
}