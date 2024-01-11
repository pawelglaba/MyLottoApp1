package com.example.mylottoapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


public class ReminderBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        var notificationBuilder = NotificationCompat.Builder(context, "ChannelId")
        notificationBuilder
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Lotto Game")
            .setContentText("Remember to check your score")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val channel = NotificationChannel("ChannelId" , "ChannelId", NotificationManager.IMPORTANCE_DEFAULT)
        NotificationManagerCompat.from(context).createNotificationChannel(channel)


        val manager: NotificationManagerCompat = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        manager.notify(200, notificationBuilder.build())

            }
    }

