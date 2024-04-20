package io.github.ashishthehulk.livpol

import android.content.Intent
import android.content.Intent.getIntent
import android.graphics.DrawFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var cardView: TextView
    private var allPoints: ArrayList<LatLng> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        cardView = findViewById(R.id.designBar)
        cardView.setOnClickListener {
//            val intent = Intent(this, DataActivity::class.java)
            val intent = Intent(this, FetchEpicData::class.java)

            startActivity(intent)
        }
    }

    override fun onMapReady(mMap: GoogleMap) {
        this.mMap = mMap
        val cameraPosition = CameraPosition.Builder()
            .target(
                LatLng(29.210496, 77.018802)
            ) // Sets the center of the map to location user
            .zoom(15f) // Sets the zoom
            .bearing(90f) // Sets the orientation of the camera to east
            .tilt(40f) // Sets the tilt of the camera to 30 degrees
            .build() // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        mMap.setOnMapClickListener {
            allPoints.add(it)
//            mMap.clear()
            mMap.addMarker(MarkerOptions()
                .position(it)
            )
        }
        mMap.setOnMarkerClickListener { marker ->
            startActivity(Intent(this, DataActivity::class.java))
            true
        }
    }

    override fun onBackPressed() {
        mMap.clear()
        super.onBackPressed()

    }
}