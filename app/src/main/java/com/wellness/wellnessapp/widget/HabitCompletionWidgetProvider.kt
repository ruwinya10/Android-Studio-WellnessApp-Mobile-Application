package com.wellness.wellnessapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.wellness.wellnessapp.R
import com.wellness.wellnessapp.activities.MainActivity
import com.wellness.wellnessapp.utils.SharedPrefManager

class HabitCompletionWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            updateWidget(context, appWidgetManager, id)
        }
    }

    companion object {
        fun updateWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
            val sharedPref = SharedPrefManager(context)
            val habits = sharedPref.getHabits()
            val total = habits.size
            val completed = habits.count { it.completed }
            val percent = if (total > 0) (completed * 100 / total) else 0

            val views = RemoteViews(context.packageName, R.layout.widget_habit_completion)
            views.setTextViewText(R.id.tv_widget_percent, "$percent%")
            views.setTextViewText(R.id.tv_widget_label, "Habits done today")

            // tap → open MainActivity
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.tv_widget_percent, pendingIntent)

            manager.updateAppWidget(widgetId, views)
        }

        // Helper to trigger manual refresh from anywhere
        fun refreshAllWidgets(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, HabitCompletionWidgetProvider::class.java))
            for (id in ids) updateWidget(context, manager, id)
        }
    }
}
