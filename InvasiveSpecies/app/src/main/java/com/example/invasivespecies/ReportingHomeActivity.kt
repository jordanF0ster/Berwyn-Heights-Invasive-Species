package com.example.invasivespecies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private lateinit var createReportButton : Button
private lateinit var viewReportsButton: Button
private lateinit var viewMyReportsButton: Button
private lateinit var signOutButton: Button

class ReportingHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporting_home)

        createReportButton = findViewById(R.id.createReportButton)
        viewReportsButton = findViewById(R.id.viewReportsButton)
        viewMyReportsButton = findViewById(R.id.viewMyReportsButton)
        signOutButton = findViewById(R.id.signOutButton)

        createReportButton.setOnClickListener { createReport() }
        viewReportsButton.setOnClickListener { viewReports(VIEW_ALL) }
        viewMyReportsButton.setOnClickListener { viewReports(VIEW_PERSONAL) }
        signOutButton.setOnClickListener { signOutAccount() }
    }

    private fun signOutAccount() {
        Firebase.auth.signOut()
        startActivity(
            Intent(
                this,
                LoginActivity::class.java
            )
        )
        finish()
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

    private fun viewReports(type: String) {
        val intent = Intent(
            this,
            ViewReports::class.java
        )
        intent.putExtra(TYPE, type)
        startActivity(
            intent
        )
    }

    companion object {
        const val TYPE = "type"
        const val VIEW_ALL = "all"
        const val VIEW_PERSONAL = "personal"
    }
}