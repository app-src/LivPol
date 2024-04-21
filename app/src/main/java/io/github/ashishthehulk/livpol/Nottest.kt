package io.github.ashishthehulk.livpol

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class Nottest : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_activity)

        createNotificationChannel()
        sendNotification()


    }

    //notifications
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun sendNotification() {
        createNotificationChannel()

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notif)  // Set notification icon (ensure notificationIcon is not null)
            .setContentTitle("Voting Reminder")  // Set notification title
            .setContentText("Don't forget to visit your polling station today!")  // Set notification text
            .setStyle(NotificationCompat.BigTextStyle().bigText("Remember, your vote is your voice. Make it heard!"))  // Optional: Expandable notification text
            .setPriority(NotificationCompat.PRIORITY_HIGH)  // Set notification priority

        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request POST_NOTIFICATIONS permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                /* Replace with a unique request code */ 101  // Example request code
            )
            return
        }
        notificationManager.notify(1, notificationBuilder.build())  // Send notification with an ID of 1
    }



    private val CHANNEL_ID = "voting_reminder"
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Voting Reminder Channel"
            val descriptionText = "Testing"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}