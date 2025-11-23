package com.wellness.wellnessapp.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.wellness.wellnessapp.R
import com.wellness.wellnessapp.services.HydrationAlarmReceiver
import com.wellness.wellnessapp.utils.SharedPrefManager

class SettingsFragment : Fragment() {

    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var textIntervalValue: TextView
    private lateinit var switchReminders: Switch
    private lateinit var btnSetInterval: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefManager = SharedPrefManager(requireContext())
        textIntervalValue = view.findViewById(R.id.text_interval_value)
        switchReminders = view.findViewById(R.id.switch_reminders)
        btnSetInterval = view.findViewById(R.id.btn_set_interval)

        loadSettings()
        setupListeners()
    }

    // Loads previously saved settings (interval + toggle state)
    private fun loadSettings() {
        val interval = sharedPrefManager.getHydrationInterval()
        val remindersEnabled = sharedPrefManager.areRemindersEnabled()

        textIntervalValue.text = formatInterval(interval)
        switchReminders.isChecked = remindersEnabled
    }

    // Listener to detect interval changes
    private fun setupListeners() {
        btnSetInterval.setOnClickListener {
            showIntervalPickerDialog()
        }

        switchReminders.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefManager.setRemindersEnabled(isChecked)
            val interval = sharedPrefManager.getHydrationInterval()

            if (isChecked) {
                Toast.makeText(
                    requireContext(),
                    "Hydration reminders enabled: ${formatInterval(interval)}",
                    Toast.LENGTH_SHORT
                ).show()
                scheduleAlarm(interval)
            } else {
                cancelAlarm()
                Toast.makeText(requireContext(), "Hydration reminders disabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //show the time picker dialog
    private fun showIntervalPickerDialog() {
        val currentInterval = sharedPrefManager.getHydrationInterval()   // Current interval in minutes
        var currentHours = currentInterval / 60       // Convert minutes to hours
        var currentMinutes = currentInterval % 60     // Remaining minutes

        val dialogView = layoutInflater.inflate(R.layout.dialog_time_picker, null)
        val pickerHours = dialogView.findViewById<android.widget.NumberPicker>(R.id.picker_hours)
        val pickerMinutes = dialogView.findViewById<android.widget.NumberPicker>(R.id.picker_minutes)

        // Configure hours picker
        pickerHours.minValue = 0
        pickerHours.maxValue = 23
        pickerHours.value = currentHours

        // Configure minutes picker
        pickerMinutes.minValue = 0
        pickerMinutes.maxValue = 59
        pickerMinutes.value = if (currentMinutes == 0 && currentHours == 0) 1 else currentMinutes  // Avoid 0+0

        // Build dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Set Reminder Interval")
            .setView(dialogView)
            .setPositiveButton("Save", null)  // We'll override click to control enabling
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

        // Get the Save button to control its enabled state
        val btnSave = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        fun updateSaveButtonState() {
            val totalMinutes = pickerHours.value * 60 + pickerMinutes.value
            btnSave.isEnabled = totalMinutes > 0  // Enable only if interval > 0
        }

        updateSaveButtonState()  // Initial check

        // Listen for picker changes
        pickerHours.setOnValueChangedListener { _, _, _ -> updateSaveButtonState() }
        pickerMinutes.setOnValueChangedListener { _, _, _ -> updateSaveButtonState() }

        btnSave.setOnClickListener {
            val selectedHours = pickerHours.value
            val selectedMinutes = pickerMinutes.value
            val totalMinutes = (selectedHours * 60) + selectedMinutes

            sharedPrefManager.setHydrationInterval(totalMinutes)
            textIntervalValue.text = formatInterval(totalMinutes)

            if (switchReminders.isChecked) {       // Reschedule alarm if reminders are on
                Toast.makeText(
                    requireContext(),
                    "Hydration reminder set: ${formatInterval(totalMinutes)}",
                    Toast.LENGTH_SHORT
                ).show()
                scheduleAlarm(totalMinutes)
            }
            dialog.dismiss()
        }
    }

    // Formats minutes into a readable string like "1 hour 30 minutes"
    private fun formatInterval(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return when {
            hours == 0 -> "$mins minute${if (mins > 1) "s" else ""}"
            mins == 0 -> "$hours hour${if (hours > 1) "s" else ""}"
            else -> "$hours hour${if (hours > 1) "s" else ""} $mins minute${if (mins > 1) "s" else ""}"
        }
    }

    // Sets up a repeating hydration reminder
    private fun scheduleAlarm(intervalMinutes: Int) {
        val context = requireContext()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HydrationAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)   // Cancels any existing alarms to prevent duplicates

        val triggerTime = System.currentTimeMillis() + intervalMinutes * 60 * 1000L  // Calculates next reminder time in milliseconds

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {  // Android 12+ requires special permission
                if (alarmManager.canScheduleExactAlarms()) {   // Checks if exact alarms are allowed
                    alarmManager.setExactAndAllowWhileIdle(   // Schedules precise alarm even in doze mode
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                } else {
                    Toast.makeText(    // Warns user if exact alarms are disabled
                        context,
                        "Exact alarms disabled. Enable in system settings for better accuracy.",
                        Toast.LENGTH_LONG
                    ).show()
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }

            Toast.makeText(
                context,
                "Next reminder in ${formatInterval(intervalMinutes)}",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: SecurityException) {   // Handles missing permission exception
            Toast.makeText(
                context,
                "Exact alarm permission required. Please enable in settings.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Cancels any active hydration reminders
    private fun cancelAlarm() {
        val context = requireContext()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HydrationAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
