package com.wellness.wellnessapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.wellness.wellnessapp.R
import com.wellness.wellnessapp.activities.MainActivity
import com.wellness.wellnessapp.models.Habit
import com.wellness.wellnessapp.models.MoodEntry
import com.wellness.wellnessapp.utils.AuthManager
import com.wellness.wellnessapp.utils.SharedPrefManager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var tvCompletionPercentage: TextView
    private lateinit var tvCompletionText: TextView
    private lateinit var btnViewHabits: Button
    private lateinit var btnLogMood: Button
    private lateinit var btnTrackSteps: Button
    private lateinit var chart: LineChart

    // New views
    private lateinit var tvGreeting: TextView
    private lateinit var tvWelcome: TextView
    private var ivMenu: ImageView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPrefManager = SharedPrefManager(requireContext())

        initViews(view)

        // --- set dynamic username from AuthManager ---
        val authManager = AuthManager(requireContext())
        val username = authManager.getCurrentUser().ifBlank { "User" }
        tvGreeting.text = "Hello $username"

        // --- menu icon opens MenuFragment (safe, checks ivMenu exist) ---
        ivMenu?.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right,
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
                )
                .replace(R.id.fragment_container, MenuFragment())
                .addToBackStack(null)
                .commit()
        }

        updateCompletionPercentage()
        setupClickListeners()
        setupMoodTrendChart()
    }

    private fun initViews(view: View) {
        tvCompletionPercentage = view.findViewById(R.id.tv_completion_percentage)
        tvCompletionText = view.findViewById(R.id.tv_completion_text)
        btnViewHabits = view.findViewById(R.id.btn_view_habits)
        btnLogMood = view.findViewById(R.id.btn_log_mood)
        btnTrackSteps = view.findViewById(R.id.btn_track_steps)
        chart = view.findViewById(R.id.chart_mood_trend)

        // new views from the updated layout (ids must match your fragment_home.xml)
        tvGreeting = view.findViewById(R.id.tvGreeting)            // "Hello <user>"
        tvWelcome = view.findViewById(R.id.tvWelcome)              // "Welcome to WellNest"
        ivMenu = view.findViewById(R.id.ivMenu) ?: view.findViewById(R.id.ivMenu)
    }

    private fun updateCompletionPercentage() {
        val habits: List<Habit> = sharedPrefManager.getHabits()
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

    private fun setupMoodTrendChart() {
        val moodEntries: List<MoodEntry> = sharedPrefManager.getMoodEntries()

        val dailyAverages = calculateDailyMoodAverages(moodEntries)
        val entries = ArrayList<Entry>()
        val dayLabels = arrayOf("6d", "5d", "4d", "3d", "2d", "Yest", "Today")

        // Fill chart entries with Float indices
        for (i in 0..6) {
            val avg: Float = dailyAverages[i] ?: 0f
            entries.add(Entry(i.toFloat(), avg)) // i.toFloat() ensures type match
        }

        val dataSet = LineDataSet(entries, "Mood Trend").apply {
            color = android.graphics.Color.parseColor("#7B4CBB")
            lineWidth = 3f
            mode = LineDataSet.Mode.CUBIC_BEZIER

            setDrawCircles(true)
            circleRadius = 5f
            setCircleColor(android.graphics.Color.parseColor("#7B4CBB"))
            circleHoleColor = android.graphics.Color.WHITE

            // Enable emoji labels
            setDrawValues(true)
            valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getPointLabel(entry: Entry?): String {
                    return entry?.y?.let { getEmojiForValue(it) } ?: ""
                }
            }

            // Gradient fill
            setDrawFilled(true)
            fillDrawable = resources.getDrawable(R.drawable.chart_gradient, null)

            highLightColor = android.graphics.Color.parseColor("#9F7AEA")
            setDrawHighlightIndicators(false)
        }

        val lineData = LineData(dataSet)
        chart.data = lineData

        // Chart styling
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setScaleEnabled(false)
        chart.setPinchZoom(false)
        chart.setExtraOffsets(10f, 10f, 10f, 10f)
        chart.setBackgroundColor(android.graphics.Color.WHITE)
        chart.setNoDataText("No mood data available yet.")
        chart.setNoDataTextColor(android.graphics.Color.GRAY)
        chart.animateX(1200)

        // X-Axis
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(dayLabels)
        xAxis.textColor = android.graphics.Color.parseColor("#301934")
        xAxis.textSize = 12f
        xAxis.setDrawGridLines(false)
        xAxis.axisLineColor = android.graphics.Color.parseColor("#9F7AEA")
        xAxis.labelRotationAngle = -15f
        xAxis.yOffset = 8f

        // Y-Axis
        chart.axisRight.isEnabled = false
        val yAxis = chart.axisLeft
        yAxis.textColor = android.graphics.Color.parseColor("#301934")
        yAxis.axisLineColor = android.graphics.Color.parseColor("#9F7AEA")
        yAxis.gridColor = android.graphics.Color.parseColor("#E4D7F7")
        yAxis.enableGridDashedLine(10f, 8f, 0f)
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 5f

        chart.legend.isEnabled = false
        chart.invalidate()
    }

    // Converts numeric mood value to emoji
    private fun getEmojiForValue(value: Float): String {
        return when (value) {
            in 4f..5f -> listOf("😄", "🤩").random()
            in 3f..4f -> listOf("😊", "💪").random()
            in 2f..3f -> "😐"
            in 1f..2f -> listOf("😔", "😴").random()
            else -> listOf("😢", "😡", "🤒").random()
        }
    }

    private fun calculateDailyMoodAverages(moodEntries: List<MoodEntry>): Map<Int, Float?> {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val today = Date()
        val todayCal = Calendar.getInstance().apply { time = today }
        val sevenDaysAgoCal = Calendar.getInstance().apply { timeInMillis = todayCal.timeInMillis }
        sevenDaysAgoCal.add(Calendar.DAY_OF_YEAR, -6) // 6 days ago to today = 7 days

        val dailyScores = mutableMapOf<String, MutableList<Float>>()

        moodEntries.forEach { entry ->
            try {
                val entryDate = sdf.parse(entry.dateTime)
                entryDate?.let {
                    if (it.time >= sevenDaysAgoCal.timeInMillis && it.time <= today.time) {
                        val entryCal = Calendar.getInstance().apply { time = it }
                        val dayKey = "${entryCal.get(Calendar.DAY_OF_YEAR)}${entryCal.get(Calendar.YEAR)}"
                        val score = getMoodScore(entry.moodEmoji)
                        dailyScores.getOrPut(dayKey) { mutableListOf() }.add(score)
                    }
                }
            } catch (e: Exception) {
                // Handle parse error
            }
        }

        val averages = mutableMapOf<Int, Float?>()
        for (i in 0..6) {
            val daysAgo = 6 - i
            val targetCal = Calendar.getInstance().apply { timeInMillis = todayCal.timeInMillis }
            targetCal.add(Calendar.DAY_OF_YEAR, -daysAgo)
            val targetKey = "${targetCal.get(Calendar.DAY_OF_YEAR)}${targetCal.get(Calendar.YEAR)}"
            val avg = dailyScores[targetKey]?.average()?.toFloat()
            averages[i] = avg
        }

        return averages
    }

    private fun getMoodScore(emoji: String): Float {
        return when (emoji) {
            "😄", "🤩" -> 5f
            "😊", "💪" -> 4f
            "😐" -> 3f
            "😔", "😴" -> 2f
            "😢", "😡", "🤒" -> 1f
            else -> 3f
        }
    }

    override fun onResume() {
        super.onResume()
        updateCompletionPercentage()
        setupMoodTrendChart()
    }
}
