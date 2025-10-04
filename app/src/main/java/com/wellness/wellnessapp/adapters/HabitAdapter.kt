package com.wellness.wellnessapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.wellness.wellnessapp.models.Habit
import com.wellness.wellnessapp.R

class HabitAdapter(
    private var habits: List<Habit>,
    private val onHabitUpdated: (Habit) -> Unit,
    private val onHabitDeleted: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_habit_name)
        private val tvDescription: TextView = itemView.findViewById(R.id.tv_habit_description)
        private val tvProgress: TextView = itemView.findViewById(R.id.tv_habit_progress)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar_habit)
        private val cbCompleted: CheckBox = itemView.findViewById(R.id.cb_completed)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete)
        private val btnIncrement: Button = itemView.findViewById(R.id.btn_increment)

        fun bind(habit: Habit) {
            tvName.text = habit.name
            tvDescription.text = habit.description
            tvProgress.text = "${habit.currentCount}/${habit.targetCount}"
            progressBar.max = habit.targetCount
            progressBar.progress = habit.currentCount
            cbCompleted.isChecked = habit.completed

            cbCompleted.setOnCheckedChangeListener { _, isChecked ->
                val updatedHabit = habit.copy(
                    completed = isChecked,
                    currentCount = if (isChecked) habit.targetCount else habit.currentCount
                )
                onHabitUpdated(updatedHabit)
            }

            btnDelete.setOnClickListener {
                onHabitDeleted(habit)
            }

            btnIncrement.setOnClickListener {
                if (habit.currentCount < habit.targetCount) {
                    val newCount = habit.currentCount + 1
                    val updatedHabit = habit.copy(
                        currentCount = newCount,
                        completed = newCount >= habit.targetCount
                    )
                    onHabitUpdated(updatedHabit)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }

    override fun getItemCount() = habits.size

    fun updateData(newHabits: List<Habit>) {
        habits = newHabits
        notifyDataSetChanged()
    }
}