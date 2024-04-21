package io.github.ashishthehulk.livpol

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class DataActivity : AppCompatActivity() {
//    private lateinit var goBack: ImageView
    private lateinit var people: TextView
    private lateinit var minutes: TextView
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
//    private lateinit var circles: ImageView
    private lateinit var context: Context
    private var notified = false

    private lateinit var barchart : BarChart
    private lateinit var peopleProgressBar : ProgressBar
    private lateinit var minutesProgressBar : ProgressBar

    var lastNotificationTime: Long = 0


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)
        context = this
//        goBack = findViewById(R.id.backBtn)
        people = findViewById(R.id.people)
        minutes = findViewById(R.id.minutes)
        barchart = findViewById(R.id.barchart)
        peopleProgressBar = findViewById(R.id.peopleProgressBar)
        minutesProgressBar = findViewById(R.id.minutesProgressBar)
//        circles = findViewById(R.id.imageView3)
//        goBack.setOnClickListener {
//            onBackPressed()
//        }

        createBarChart()
        createNotificationChannel()
        sendNotification()


        // Write a message to the database
        val database = Firebase.database
        val myRef = database.getReference("people")

        myRef.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                val value = snapshot.getValue<HashMap<String, String>>().toString()
                Log.d("hi", "Value is: " + value)
                people.text = value.replaceFirst("{Name=", "").replaceFirst("}", "") + " \npeople"
                minutes.text = (value.replaceFirst("{Name=", "").replaceFirst("}", "").toInt() * 2).toString() + " \nmins"

                peopleProgressBar.progress = value.replaceFirst("{Name=", "").replaceFirst("}", "").toInt()
                Log.v("hi", peopleProgressBar.progress.toString())
                minutesProgressBar.progress = value.replaceFirst("{Name=", "").replaceFirst("}", "").toInt()*3
                Log.v("hi", minutesProgressBar.progress.toString())



                var v = value.replaceFirst("{Name=", "").replaceFirst("}", "").toInt()
//                runOnUiThread(Runnable{
//                    if(v<4){
//                      circles.setImageDrawable(getDrawable(R.drawable.data_design1))
//                    }else circles.setImageDrawable(getDrawable(R.drawable.data_design))
//                })

                // Check condition and send notification only if 10 seconds have passed since last notification
                if (v in 1..3) {
                    // Calculate the time difference in milliseconds since the last notification
                    val currentTime = SystemClock.elapsedRealtime()
                    val timeDifference = currentTime - lastNotificationTime

                    // Check if 10 seconds (10000 milliseconds) have passed since the last notification
                    if (timeDifference >= 10000) {
                        // Trigger notification
                        sendNotification()
                    }
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("ji", "Failed to read value.", error.toException())
            }

        })



    }

    //notifications
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun sendNotification() {
        createNotificationChannel()

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notif)  // Set notification icon (ensure notificationIcon is not null)
            .setContentTitle("Voting Reminder")  // Set notification title
//            .setContentText("Don't forget to visit your polling station today!")  // Set notification text
            .setContentText("Less crowd detected at your polling station!!")  // Set notification text
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
        lastNotificationTime = SystemClock.elapsedRealtime()

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

    private fun createBarChart() {
        barchart = findViewById(R.id.barchart)

        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f,10f))
        entries.add(BarEntry(1f,15f))
        entries.add(BarEntry(2f,8f))
        entries.add(BarEntry(3f,11f))
        entries.add(BarEntry(4f,30f))
        entries.add(BarEntry(5f,45f))
        entries.add(BarEntry(6f,25f))
        entries.add(BarEntry(7f,27f))
        entries.add(BarEntry(8f,35f))
        entries.add(BarEntry(9f,20f))
        entries.add(BarEntry(10f,16f))

        val barDataSet = BarDataSet(entries,"Time")

        val dataSet = BarData(barDataSet)

        dataSet.barWidth = 0.9f

        barchart.description.text = "People"
        barchart.data = dataSet
        barchart.setFitBars(true)
        barchart.invalidate()
    }
}
