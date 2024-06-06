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

/**
 * BroadcastReceiver odpowiedzialny za wysyłanie powiadomień przypominających o sprawdzeniu wyników lotto.
 */
class ReminderBroadcast : BroadcastReceiver() {

    /**
     *
     * @param context Kontekst aplikacji.
     * @param intent Intent, który spowodował wywołanie odbiornika.
     */

    override fun onReceive(context: Context, intent: Intent) {
        // Konfiguracja powiadomienia
        val notificationBuilder = NotificationCompat.Builder(context, "ChannelId")
        notificationBuilder
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Lotto Game")
            .setContentText("Remember to check your score")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Utworzenie kanału powiadomień
        val channel = NotificationChannel("ChannelId", "ChannelId", NotificationManager.IMPORTANCE_DEFAULT)
        NotificationManagerCompat.from(context).createNotificationChannel(channel)

        val manager: NotificationManagerCompat = NotificationManagerCompat.from(context)

        // Sprawdzenie uprawnień do wysyłania powiadomień
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            return
        }

        // Wyślij powiadomienie
        manager.notify(200, notificationBuilder.build())
    }
}
