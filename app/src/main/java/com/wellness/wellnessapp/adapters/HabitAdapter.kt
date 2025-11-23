package com.wellness.wellnessapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.wellness.wellnessapp.models.Habit
import com.wellness.wellnessapp.R

class HabitAdapter(  //the link between your data (the Habit list) and the RecyclerView UI that displays each habit on screen.
    private var habits: List<Habit>,
    private val onHabitUpdated: (Habit) -> Unit,
    private val onHabitDeleted: (Habit) -> Unit,
    private val onHabitEdit: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {  //holds references to the UI elements inside a single list item
        private val tvName: TextView = itemView.findViewById(R.id.tv_habit_name)
        private val tvDescription: TextView = itemView.findViewById(R.id.tv_habit_description)
        private val cbCompleted: CheckBox = itemView.findViewById(R.id.cb_completed)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btn_edit)

        //binds (connects) the data from one Habit object to the UI widgets for that row.
        fun bind(habit: Habit) {
            tvName.text = habit.name
            tvDescription.text = habit.description

            // Avoid triggering listener during bind due to view recycling
            cbCompleted.setOnCheckedChangeListener(null)
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

            btnEdit.setOnClickListener { onHabitEdit(habit) }
        }
    }

    //Called when a new list item view needs to be created
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    //Called for each visible item
    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }

    //Returns how many items are in the list
    override fun getItemCount() = habits.size

    //Used to refresh the list when data changes
    fun updateData(newHabits: List<Habit>) {
        habits = newHabits
        notifyDataSetChanged()
    }
}