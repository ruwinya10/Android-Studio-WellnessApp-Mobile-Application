package com.wellness.wellnessapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.wellness.wellnessapp.R
import com.wellness.wellnessapp.services.HydrationReminderWorker
import com.wellness.wellnessapp.utils.SharedPrefManager
import java.util.concurrent.TimeUnit

class SettingsFragment : Fragment() {
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var seekbarInterval: SeekBar
    private lateinit var textIntervalValue: TextView
    private lateinit var switchReminders: Switch

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPrefManager = SharedPrefManager(requireContext())

        seekbarInterval = view.findViewById(R.id.seekbar_interval)
        textIntervalValue = view.findViewById(R.id.text_interval_value)
        switchReminders = view.findViewById(R.id.switch_reminders)

        loadSettings()
        setupListeners()
    }

    private fun loadSettings() {
        val interval = sharedPrefManager.getHydrationInterval()
        val remindersEnabled = sharedPrefManager.areRemindersEnabled()

        seekbarInterval.progress = (interval / 15) - 1 // Convert to 0-based index for 15-min increments
        textIntervalValue.text = "$interval minutes"
        switchReminders.isChecked = remindersEnabled
    }

    private fun setupListeners() {
        seekbarInterval.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val interval = (progress + 1) * 15 // Minimum 15 minutes
                textIntervalValue.text = "$interval minutes"
                sharedPrefManager.setHydrationInterval(interval)
                if (switchReminders.isChecked) {
                    scheduleReminders(interval)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        switchReminders.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefManager.setRemindersEnabled(isChecked)
            if (isChecked) {
                val interval = sharedPrefManager.getHydrationInterval()
                scheduleReminders(interval)
            } else {
                cancelReminders()
            }
        }
    }

    private fun scheduleReminders(intervalMinutes: Int) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val reminderRequest = PeriodicWorkRequestBuilder<HydrationReminderWorker>(
            intervalMinutes.toLong(), TimeUnit.MINUTES
        ).setConstraints(constraints)
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "hydration_reminder",
            ExistingPeriodicWorkPolicy.REPLACE,
            reminderRequest
        )
    }

    private fun cancelReminders() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("hydration_reminder")
    }
}