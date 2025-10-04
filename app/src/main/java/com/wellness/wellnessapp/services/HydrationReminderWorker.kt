package com.wellness.wellnessapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.wellness.wellnessapp.R

class HydrationReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        if (isRemindersEnabled()) {
            showNotification()
        }
        return Result.success()
    }

    private fun isRemindersEnabled(): Boolean {
        val sharedPref = applicationContext.getSharedPreferences("WellnessApp", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("reminders_enabled", true)
    }

    private fun showNotification() {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "hydration_reminder",
                "Hydration Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders to drink water regularly"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, "hydration_reminder")
            .setContentTitle("💧 Time to Hydrate!")
            .setContentText("Don't forget to drink water to stay hydrated and healthy!")
            .setSmallIcon(R.drawable.ic_water_drop)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }
}