package io.github.ashishthehulk.livpol

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"
    private lateinit var barchart : BarChart
    private lateinit var peopleProgressBar : ProgressBar
    private lateinit var minutesProgressBar : ProgressBar

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
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        // Write a message to the database
        val database = Firebase.database
        val myRef = database.getReference("people")

        myRef.addValueEventListener(object : ValueEventListener {
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

                var pendingIntent: PendingIntent? = null
                pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_MUTABLE
                    )
                } else {
                    PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }

//                val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                var v = value.replaceFirst("{Name=", "").replaceFirst("}", "").toInt()
//                runOnUiThread(Runnable{
//                    if(v<4){
//                      circles.setImageDrawable(getDrawable(R.drawable.data_design1))
//                    }else circles.setImageDrawable(getDrawable(R.drawable.data_design))
//                })

                // RemoteViews are used to use the content of
                // some different layout apart from the current activity layout
//                val contentView = RemoteViews(packageName, R.layout.activity_after_notification)

                if (!notified) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        notificationChannel = NotificationChannel(
                            channelId,
                            description,
                            NotificationManager.IMPORTANCE_HIGH
                        )
                        notificationChannel.enableLights(true)
                        notificationChannel.lightColor = Color.GREEN
                        notificationChannel.enableVibration(false)
                        notificationManager.createNotificationChannel(notificationChannel)

                        builder = Notification.Builder(context, channelId)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setLargeIcon(
                                BitmapFactory.decodeResource(
                                    context.resources,
                                    R.drawable.ic_launcher_background
                                )
                            )
                            .setContentIntent(pendingIntent)
                    } else {

                        builder = Notification.Builder(context)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setLargeIcon(
                                BitmapFactory.decodeResource(
                                    context.resources,
                                    R.drawable.ic_launcher_background
                                )
                            )
                            .setContentIntent(pendingIntent)
                    }
                    notificationManager.notify(1234, builder.build())
                    notified = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("ji", "Failed to read value.", error.toException())
            }

        })



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
