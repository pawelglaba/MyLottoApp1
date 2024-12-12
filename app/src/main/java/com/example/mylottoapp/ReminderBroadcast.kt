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
 * BroadcastReceiver responsible for sending reminder notifications to check lotto results.
 *
 * ### Introduction to BroadcastReceiver:
 * A BroadcastReceiver is a component that responds to system-wide broadcast messages or intents.
 * In this example, the `ReminderBroadcast` listens for a specific broadcast and sends a notification to the user.
 *
 * BroadcastReceivers are commonly used for tasks such as:
 * - Responding to scheduled alarms.
 * - Handling system events like connectivity changes or device boot.
 * - Sending notifications based on external triggers.
 */
class ReminderBroadcast : BroadcastReceiver() {

    /**
     * Handles the broadcast intent and sends a notification.
     *
     * ### onReceive:
     * This method is triggered when the BroadcastReceiver receives a broadcast intent.
     * It processes the intent and performs the required action, in this case, sending a notification.
     *
     * @param context The application context, used to access resources and system services.
     * @param intent The intent that triggered the BroadcastReceiver.
     */
    override fun onReceive(context: Context, intent: Intent) {
        // Step 1: Configure the notification
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo) // Icon for the notification
            .setContentTitle("Lotto Game") // Title of the notification
            .setContentText("Remember to check your score") // Notification message
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Priority determines how notifications behave

        // Step 2: Create the notification channel (required for Android 8.0 and above)
        val channel = NotificationChannel(
            CHANNEL_ID, // Unique channel ID
            "Lotto Reminder Channel", // Human-readable channel name
            NotificationManager.IMPORTANCE_DEFAULT // Importance level of the notifications in this channel
        )
        NotificationManagerCompat.from(context).createNotificationChannel(channel)

        // Step 3: Obtain the NotificationManagerCompat instance
        val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)

        // Step 4: Check notification permissions (Android 13 and above)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // If permissions are not granted, exit without sending the notification
            return
        }

        // Step 5: Send the notification
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    /**
     * Companion object to hold constant values for the NotificationChannel and Notification ID.
     *
     * ### Why use a companion object?
     * Companion objects in Kotlin are used to define static members that belong to the class rather than a specific instance.
     */
    companion object {
        private const val CHANNEL_ID = "LottoReminderChannel" // Unique identifier for the notification channel
        private const val NOTIFICATION_ID = 200 // Unique ID for the notification, used to update or cancel it later
    }
}
