package com.example.invasivespecies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

private lateinit var createReportButton : Button
private lateinit var viewReportsButton: Button

class ReportingHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporting_home)

        createReportButton = findViewById(R.id.createReportButton)
        viewReportsButton = findViewById(R.id.viewReportsButton)

        createReportButton.setOnClickListener { createReport() }
    }

    private fun createReport() {
        startActivityForResult(
            Intent(
                this,
                ReportingActivity::class.java
            ),
            ReportingActivity.ADD_REPORT_REQUEST
        )
    }


}