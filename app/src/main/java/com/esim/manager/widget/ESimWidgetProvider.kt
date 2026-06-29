package com.esim.manager.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.esim.manager.R
import com.esim.manager.ui.MainActivity

class ESimWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, ESimWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            
            // Notify ListView to refresh data
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetListView)
            
            // Redraw widgets
            for (id in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, id)
            }
        }
    }

    companion object {
        const val ACTION_REFRESH = "com.esim.manager.ACTION_WIDGET_REFRESH"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            // ── 1. Read Theme / Background Color Style ────────────────────────
            val prefs = context.getSharedPreferences("esim_manager_prefs", Context.MODE_PRIVATE)
            val bgStyle = prefs.getString("widget_bg_style", "blue") ?: "blue"

            val bgResId = when (bgStyle) {
                "white" -> R.drawable.bg_widget_container_white
                "dark" -> R.drawable.bg_widget_container_dark
                "green" -> R.drawable.bg_widget_container_green
                "orange" -> R.drawable.bg_widget_container_orange
                "purple" -> R.drawable.bg_widget_container_purple
                "pink" -> R.drawable.bg_widget_container_pink
                "yellow" -> R.drawable.bg_widget_container_yellow
                "teal" -> R.drawable.bg_widget_container_teal
                "grey" -> R.drawable.bg_widget_container_grey
                else -> R.drawable.bg_widget_container_blue // default
            }

            // Set background resource dynamically using setInt
            views.setInt(R.id.widgetRoot, "setBackgroundResource", bgResId)

            // Set title and icon colors based on chosen background style
            val titleColor = when (bgStyle) {
                "dark" -> 0xFFFFFFFF.toInt()
                "white" -> 0xFF1A73E8.toInt()
                "green" -> 0xFF137333.toInt()
                "orange" -> 0xFFE65100.toInt() // Dark Orange
                "purple" -> 0xFF4A148C.toInt() // Dark Purple
                "pink" -> 0xFFC2185B.toInt() // Dark Pink
                "yellow" -> 0xFFF57F17.toInt() // Dark Yellow/Amber
                "teal" -> 0xFF004D40.toInt() // Dark Teal
                "grey" -> 0xFF212121.toInt() // Dark Grey
                else -> 0xFF1A73E8.toInt()
            }
            views.setTextColor(R.id.tvWidgetTitle, titleColor)
            views.setInt(R.id.btnWidgetAdd, "setColorFilter", titleColor)

            // ── 2. Click on header → open MainActivity ────────────────────────
            val mainIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val headerPendingIntent = PendingIntent.getActivity(
                context, 0, mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widgetTitleLayout, headerPendingIntent)
            views.setOnClickPendingIntent(R.id.btnWidgetAdd, headerPendingIntent)

            // ── 3. Remote Service Adapter for ListView ───────────────────────
            val serviceIntent = Intent(context, ESimWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.fromParts("esim", appWidgetId.toString(), null)
            }
            views.setRemoteAdapter(R.id.widgetListView, serviceIntent)
            views.setEmptyView(R.id.widgetListView, R.id.widgetEmptyView)

            // ── 4. Item Click Template ───────────────────────────────────────
            val itemPendingIntent = PendingIntent.getActivity(
                context, 1, mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setPendingIntentTemplate(R.id.widgetListView, itemPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
