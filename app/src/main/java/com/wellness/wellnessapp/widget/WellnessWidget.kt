package com.wellness.wellnessapp.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.wellness.wellnessapp.R
import com.wellness.wellnessapp.utils.SharedPrefManager

class WellnessWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val sharedPrefManager = SharedPrefManager(context)
    val habits = sharedPrefManager.getHabits()
    val totalHabits = habits.size
    val completedHabits = habits.count { it.completed }
    val progress = if (totalHabits > 0) (completedHabits * 100 / totalHabits) else 0

    val views = RemoteViews(context.packageName, R.layout.wellness_widget)
    views.setTextViewText(R.id.widget_percentage, "$progress%")
    views.setTextViewText(R.id.widget_text, "$completedHabits/$totalHabits habits")

    // Set progress bar
    views.setProgressBar(R.id.widget_progress, 100, progress, false)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}