package com.esim.manager.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.esim.manager.data.ESimModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object ReminderManager {

    fun scheduleReminder(context: Context, esim: ESimModel) {
        if (esim.reminderDays < 0) {
            cancelReminder(context, esim.id)
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ESimReminderReceiver::class.java).apply {
            putExtra("esim_id", esim.id)
            putExtra("provider", esim.provider)
            putExtra("expiry_date", esim.expiryDate)
            putExtra("reminder_days", esim.reminderDays)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            esim.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Parse expiry date (YYYY-MM-DD)
        try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = format.parse(esim.expiryDate) ?: return
            val calendar = Calendar.getInstance()
            calendar.time = date
            
            // Set time to 09:00 AM on the day of reminder
            calendar.set(Calendar.HOUR_OF_DAY, 9)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            // Subtract reminder days
            calendar.add(Calendar.DAY_OF_MONTH, -esim.reminderDays)

            val triggerTime = calendar.timeInMillis
            // Only schedule if the trigger time is in the future
            if (triggerTime > System.currentTimeMillis()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelReminder(context: Context, esimId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ESimReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            esimId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
