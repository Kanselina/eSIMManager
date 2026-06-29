package com.esim.manager.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.esim.manager.ui.MainActivity

class ESimReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val esimId = intent.getStringExtra("esim_id") ?: return
        val provider = intent.getStringExtra("provider") ?: "eSIM"
        val expiryDate = intent.getStringExtra("expiry_date") ?: ""
        val reminderDays = intent.getIntExtra("reminder_days", 3)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "esim_expiry_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "eSIM 到期提醒",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "在 eSIM 到期前提起通知"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            esimId.hashCode(),
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "eSIM 即将到期提醒"
        val message = if (reminderDays == 0) {
            "您的 $provider eSIM 卡将于今天 ($expiryDate) 到期，请注意处理。"
        } else {
            "您的 $provider eSIM 卡还有 $reminderDays 天 ($expiryDate) 即将到期，请及时续费或关注余额。"
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()

        notificationManager.notify(esimId.hashCode(), notification)
    }
}
