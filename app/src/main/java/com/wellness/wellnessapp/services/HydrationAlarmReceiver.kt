package com.wellness.wellnessapp.services

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.wellness.wellnessapp.R
import com.wellness.wellnessapp.utils.SharedPrefManager

class HydrationAlarmReceiver : BroadcastReceiver() {  //this class receives the hydration alarm broadcast
    override fun onReceive(context: Context, intent: Intent?) {
        Toast.makeText(context, "💧 Hydration alarm triggered!", Toast.LENGTH_LONG).show()

        val sharedPref = SharedPrefManager(context)
        if (!sharedPref.areRemindersEnabled()) return

        // Build and show notification
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val notification = NotificationCompat.Builder(context, "hydration_reminder")
                .setSmallIcon(R.drawable.ic_water_drop)
                .setContentTitle("💧 Time to Hydrate!")
                .setContentText("Drink a glass of water now to stay healthy.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .build()

            NotificationManagerCompat.from(context).notify(1, notification)
        }

        // Reschedule automatically
        val interval = sharedPref.getHydrationInterval()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, HydrationAlarmReceiver::class.java)

        //Creates a PendingIntent the system can use later to launch your receiver at the right time
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        //Schedules the next reminder using the AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + interval * 60 * 1000L,
            pendingIntent
        )
    }
}
