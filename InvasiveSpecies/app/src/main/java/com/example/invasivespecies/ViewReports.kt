package com.example.invasivespecies

import android.app.Activity
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class ViewReports : Activity() {

    private lateinit var mAdapter: ReportItemAdapter
    private lateinit var mRecyclerView: RecyclerView

    private lateinit var database: FirebaseDatabase
//    private val mItems2 = ArrayList<Report>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_reports)

        database = FirebaseDatabase.getInstance()
        mRecyclerView = findViewById<RecyclerView>(R.id.list)
        mAdapter = ReportItemAdapter(this)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        getReports()
//        val x = mAdapter.itemCount
//        val y = mItems2.size
//        mRecyclerView.adapter = mAdapter
    }

    private fun getReports() {
        var reportsDBRef = database.getReference("reports")
        reportsDBRef.get().addOnSuccessListener {
             for (child in it.children){
                 for(plant in child.children) {
                     val report = Report(
                         plant.child("id").value.toString(),
                         plant.child("plantname").value.toString(),
                         plant.child("color").value.toString(),
                         plant.child("amount").value.toString(),
                         plant.child("notes").value.toString(),
                         getReportLocation(plant.child("location"))
                     )
                     mAdapter.add(report)
                     var p = mAdapter.itemCount
                     p
//                     mItems2.add(report)
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