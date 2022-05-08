package com.example.invasivespecies

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.invasivespecies.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlin.math.roundToInt

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var storageRef: StorageReference
    private lateinit var mHandler: Handler
    private var checker = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance()
        storageRef = Firebase.storage.reference
        mHandler = Handler()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val berwyn = LatLng(38.9933949,-76.9212103)
        val zoom = 15f
        val dbRef = database.getReference("locations")
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(berwyn, zoom))
        mMap.setOnMapLongClickListener { latLng ->
            val marker = mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
            )
            val lat = marker?.position?.latitude
            val lng = marker?.position?.longitude
            val id = lat?.minus(lng!!)?.roundToInt().toString()
            val location = Location(
                id,
                lat,
                lng,
            )
            dbRef.setValue(location)
            dbRef.get().addOnSuccessListener {
                for (child in it.children) {
                    val id = child.value.toString()
                    val point = LatLng(lat!!, lng!!)
                    mMap.addMarker(MarkerOptions()
                        .position(point))
                    val location = Location(
                        id,
                        lat,
                        lng,
                    )
                }
            }
            mHandler.postDelayed(Runnable {
                startActivity(
                    Intent(
                        this,
                        ReportingHomeActivity::class.java
                    )
                )
            }, 3000)
        }
    }
}