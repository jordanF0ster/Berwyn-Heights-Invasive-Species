package com.example.invasivespecies

import android.app.Activity
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ViewReports : Activity() {

    private lateinit var mAdapter: ReportItemAdapter
    private lateinit var mRecyclerView: RecyclerView

    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_reports)

        database = FirebaseDatabase.getInstance()
        mRecyclerView = findViewById<RecyclerView>(R.id.list)
        mAdapter = ReportItemAdapter(this)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        getReports()
    }

    private fun getReports() {
        val extras = intent.extras
        val type = extras!!.getString(ReportingHomeActivity.TYPE)
        var reportsDBRef = database.getReference("reports")
        val storageRef = Firebase.storage.reference
        reportsDBRef.get().addOnSuccessListener {
            if (type == ReportingHomeActivity.VIEW_ALL) {
                for (child in it.children) {
                    for (plant in child.children) {
                        val id = plant.child("id").value.toString()
                        val report = Report(
                            id,
                            plant.child("plantname").value.toString(),
                            plant.child("color").value.toString(),
                            plant.child("amount").value.toString(),
                            plant.child("notes").value.toString(),
                            getReportLocation(plant.child("location")),
                            plant.child("creator").value.toString()
                        )
                        mAdapter.add(report)
                    }
                }
            } else {
                val userName = Firebase.auth.currentUser!!.displayName
                for (child in it.children) {
                    for (plant in child.children) {
                        if (plant.child("creator").value.toString() == userName) {
                            val id = plant.child("id").value.toString()
                            val report = Report(
                                id,
                                plant.child("plantname").value.toString(),
                                plant.child("color").value.toString(),
                                plant.child("amount").value.toString(),
                                plant.child("notes").value.toString(),
                                getReportLocation(plant.child("location")),
                                plant.child("creator").value.toString()
                            )
                            mAdapter.add(report)
                        }
                    }
                }
            }
            mRecyclerView.adapter = mAdapter

        }
    }

    private fun getReportLocation(locationKey: DataSnapshot): Location? {
        val location = Location(locationKey.child("provider").value.toString())
        location.latitude = locationKey.child("latitude").value.toString().toDouble()
        location.longitude = locationKey.child("longitude").value.toString().toDouble()
        location.accuracy = locationKey.child("accuracy").value.toString().toFloat()
        location.altitude = locationKey.child("altitude").value.toString().toDouble()
        location.bearing = locationKey.child("bearing").value.toString().toFloat()
        location.bearingAccuracyDegrees =
            locationKey.child("bearingAccuracyDegrees").value.toString().toFloat()
        location.elapsedRealtimeNanos =
            locationKey.child("elapsedRealtimeNanos").value.toString().toLong()
        location.speed = locationKey.child("speed").value.toString().toFloat()
        location.speedAccuracyMetersPerSecond =
            locationKey.child("speedAccuracyMetersPerSecond").value.toString().toFloat()
        location.time = locationKey.child("time").value.toString().toLong()
        location.verticalAccuracyMeters =
            locationKey.child("verticalAccuracyMeters").value.toString().toFloat()

        return location
    }
}