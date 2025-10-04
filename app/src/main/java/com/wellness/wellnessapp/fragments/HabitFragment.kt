package com.wellness.wellnessapp.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wellness.wellnessapp.R
import com.wellness.wellnessapp.adapters.HabitAdapter
import com.wellness.wellnessapp.models.Habit
import com.wellness.wellnessapp.utils.SharedPrefManager

class HabitFragment : Fragment() {
    private lateinit var sharedPrefManager: SharedPrefManager
    private val habits = mutableListOf<Habit>()
    private lateinit var adapter: HabitAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var fabAddHabit: FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_habit, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPrefManager = SharedPrefManager(requireContext())

        // Initialize views
        recyclerView = view.findViewById(R.id.recycler_habits)
        progressText = view.findViewById(R.id.text_progress)
        progressBar = view.findViewById(R.id.progress_bar)
        fabAddHabit = view.findViewById(R.id.fab_add_habit)

        setupRecyclerView()
        loadHabits()
        setupFloatingActionButton()
    }

    private fun setupRecyclerView() {
        adapter = HabitAdapter(habits, { habit -> onHabitUpdated(habit) }, { habit -> deleteHabit(habit) })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadHabits() {
        habits.clear()
        habits.addAll(sharedPrefManager.getHabits())
        adapter.updateData(habits)
        updateProgress()
    }

    private fun onHabitUpdated(habit: Habit) {
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index != -1) {
            habits[index] = habit
            sharedPrefManager.saveHabits(habits)
            adapter.updateData(habits)
            updateProgress()
        }
    }

    private fun deleteHabit(habit: Habit) {
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index != -1) {
            habits.removeAt(index)
            sharedPrefManager.saveHabits(habits)
            adapter.updateData(habits)
            updateProgress()
        }
    }

    private fun updateProgress() {
        val totalHabits = habits.size
        val completedHabits = habits.count { it.completed }
        val progress = if (totalHabits > 0) (completedHabits * 100 / totalHabits) else 0

        progressText.text = "Progress: $progress%"
        progressBar.progress = progress
    }

    private fun setupFloatingActionButton() {
        fabAddHabit.setOnClickListener {
            showAddHabitDialog()
        }
    }

    private fun showAddHabitDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_habit, null)
        val etHabitName = dialogView.findViewById<EditText>(R.id.et_habit_name)
        val etHabitDesc = dialogView.findViewById<EditText>(R.id.et_habit_desc)
        val etTargetCount = dialogView.findViewById<EditText>(R.id.et_target_count)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Add New Habit")
            .setPositiveButton("Add") { _, _ ->
                val name = etHabitName.text.toString()
                val desc = etHabitDesc.text.toString()
                val target = etTargetCount.text.toString().toIntOrNull() ?: 1

                if (name.isNotEmpty()) {
                    val newHabit = Habit(name = name, description = desc, targetCount = target)
                    habits.add(newHabit)
                    sharedPrefManager.saveHabits(habits)
                    adapter.updateData(habits)
                    updateProgress()
                    Toast.makeText(requireContext(), "Habit added successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Please enter habit name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }
}