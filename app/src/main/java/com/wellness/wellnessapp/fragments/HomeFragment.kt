package com.wellness.wellnessapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.wellness.wellnessapp.R
import com.wellness.wellnessapp.activities.MainActivity
import com.wellness.wellnessapp.utils.SharedPrefManager

class HomeFragment : Fragment() {

    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var tvCompletionPercentage: TextView
    private lateinit var tvCompletionText: TextView
    private lateinit var btnViewHabits: Button
    private lateinit var btnLogMood: Button
    private lateinit var btnTrackSteps: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPrefManager = SharedPrefManager(requireContext())

        initViews(view)
        updateCompletionPercentage()
        setupClickListeners()
    }

    private fun initViews(view: View) {
        tvCompletionPercentage = view.findViewById(R.id.tv_completion_percentage)
        tvCompletionText = view.findViewById(R.id.tv_completion_text)
        btnViewHabits = view.findViewById(R.id.btn_view_habits)
        btnLogMood = view.findViewById(R.id.btn_log_mood)
        btnTrackSteps = view.findViewById(R.id.btn_track_steps)
    }

    private fun updateCompletionPercentage() {
        val habits = sharedPrefManager.getHabits()
        val totalHabits = habits.size
        val completedHabits = habits.count { it.completed }
        val progress = if (totalHabits > 0) (completedHabits * 100 / totalHabits) else 0

        tvCompletionPercentage.text = "$progress%"
        tvCompletionText.text = "You've completed $completedHabits out of $totalHabits habits today"
    }

    private fun setupClickListeners() {
        btnViewHabits.setOnClickListener {
            // Navigate to habits fragment
            (requireActivity() as? MainActivity)?.navigateToHabits()
        }

        btnLogMood.setOnClickListener {
            // Navigate to mood fragment
            (requireActivity() as? MainActivity)?.navigateToMood()
        }

        btnTrackSteps.setOnClickListener {
            // Navigate to steps fragment
            (requireActivity() as? MainActivity)?.navigateToSteps()
        }
    }

    override fun onResume() {
        super.onResume()
        updateCompletionPercentage()
    }
}