package com.wellness.wellnessapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wellness.wellnessapp.models.MoodEntry
import com.wellness.wellnessapp.R

class MoodAdapter(private val moodEntries: List<MoodEntry>) : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    inner class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvEmoji: TextView = itemView.findViewById(R.id.tv_mood_emoji)
        private val tvMoodText: TextView = itemView.findViewById(R.id.tv_mood_text)
        private val tvDateTime: TextView = itemView.findViewById(R.id.tv_date_time)
        private val tvNotes: TextView = itemView.findViewById(R.id.tv_notes)

        fun bind(entry: MoodEntry) {
            tvEmoji.text = entry.moodEmoji
            tvMoodText.text = entry.moodText
            tvDateTime.text = entry.dateTime
            tvNotes.text = entry.notes
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mood, parent, false)
        return MoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        holder.bind(moodEntries[position])
    }

    override fun getItemCount() = moodEntries.size
}