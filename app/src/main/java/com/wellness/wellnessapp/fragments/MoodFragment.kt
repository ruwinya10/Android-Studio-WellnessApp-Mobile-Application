package com.wellness.wellnessapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wellness.wellnessapp.R
import com.wellness.wellnessapp.adapters.MoodAdapter
import com.wellness.wellnessapp.models.MoodEntry
import com.wellness.wellnessapp.utils.SharedPrefManager

class MoodFragment : Fragment() {
    private lateinit var sharedPrefManager: SharedPrefManager
    private val moodEntries = mutableListOf<MoodEntry>()
    private lateinit var adapter: MoodAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinnerEmojis: Spinner
    private lateinit var etMoodNotes: EditText
    private lateinit var btnLogMood: Button
    private lateinit var btnShareMood: Button

    private val emojis = listOf("😊", "😄", "😐", "😔", "😢", "😡", "🤩", "😴", "🤒", "💪")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mood, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPrefManager = SharedPrefManager(requireContext())

        // Initialize views
        recyclerView = view.findViewById(R.id.recycler_mood_entries)
        spinnerEmojis = view.findViewById(R.id.spinner_emojis)
        etMoodNotes = view.findViewById(R.id.et_mood_notes)
        btnLogMood = view.findViewById(R.id.btn_log_mood)
        btnShareMood = view.findViewById(R.id.btn_share_mood)

        setupRecyclerView()
        loadMoodEntries()
        setupEmojiSelector()
        setupShareButton()
    }

    // Sets up RecyclerView and its adapter
    private fun setupRecyclerView() {
        adapter = MoodAdapter(moodEntries)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    // Loads saved mood entries from SharedPreferences
    private fun loadMoodEntries() {
        moodEntries.clear()
        moodEntries.addAll(sharedPrefManager.getMoodEntries())
        adapter.notifyDataSetChanged()
    }

    // Populates emoji spinner and sets log button functionality
    private fun setupEmojiSelector() {
        val emojiAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, emojis)
        emojiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEmojis.adapter = emojiAdapter

        btnLogMood.setOnClickListener {
            val selectedEmoji = spinnerEmojis.selectedItem as String
            val notes = etMoodNotes.text.toString()

            val moodEntry = MoodEntry(
                moodEmoji = selectedEmoji,
                moodText = getMoodText(selectedEmoji),
                notes = notes
            )

            moodEntries.add(0, moodEntry)
            sharedPrefManager.saveMoodEntries(moodEntries)
            adapter.notifyItemInserted(0)
            etMoodNotes.text.clear()

            Toast.makeText(requireContext(), "Mood logged successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    // Maps each emoji to a mood description
    private fun getMoodText(emoji: String): String {
        return when (emoji) {
            "😊" -> "Happy"
            "😄" -> "Very Happy"
            "😐" -> "Neutral"
            "😔" -> "Sad"
            "😢" -> "Very Sad"
            "😡" -> "Angry"
            "🤩" -> "Excited"
            "😴" -> "Tired"
            "🤒" -> "Sick"
            "💪" -> "Motivated"
            else -> "Unknown"
        }
    }

    // Prepares share button logic for mood sharing
    private fun setupShareButton() {
        btnShareMood.setOnClickListener {
            if (moodEntries.isNotEmpty()) {
                val latestMood = moodEntries.first()
                val shareMessage = "My current mood: ${latestMood.moodEmoji} ${latestMood.moodText}\n" +
                        "Time: ${latestMood.dateTime}\n" +
                        "Notes: ${latestMood.notes}"

                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareMessage)
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(shareIntent, "Share your mood"))
            } else {
                Toast.makeText(requireContext(), "No mood entries to share", Toast.LENGTH_SHORT).show()
            }
        }
    }
}