package com.capstone.project.nutritionguardian.notification

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.capstone.project.nutritionguardian.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val notificationMessage = when (intent.getLongExtra("notification_time", 0)) {
            (7 * 60 * 60 * 1000).toLong() -> "Waktunya sarapan!"
            (12 * 60 * 60 * 1000).toLong() -> "Waktunya makan siang!"
            (23 * 60 * 60 * 1000).toLong() -> "Waktunya makan malam!"
            else -> "Pengingat"
        }

        showNotification(context, notificationMessage)
    }

    private fun showNotification(context: Context, message: String) {
        val builder = NotificationCompat.Builder(context, "channel_id")
            .setSmallIcon(R.drawable.avatarbaby)
            .setContentTitle("Pengingat")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        NotificationManagerCompat.from(context).notify(1, builder.build())
    }
}
