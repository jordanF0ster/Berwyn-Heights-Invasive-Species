package com.example.invasivespecies

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


private lateinit var mImageView : ImageView
private lateinit var mPictureButton: Button
private lateinit var mSaveButton: Button
private lateinit var mNameEditText: EditText
private lateinit var mNotesEditText: EditText
private lateinit var mColorSpinner: Spinner
private lateinit var mAmountSpinner: Spinner

private lateinit var database: FirebaseDatabase

class ReportingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporting)

        database = FirebaseDatabase.getInstance()

        mImageView = findViewById(R.id.imageView)
        mPictureButton = findViewById(R.id.pictureButton)
        mSaveButton = findViewById(R.id.saveButton)
        mNameEditText = findViewById(R.id.textPlantName)
        mNotesEditText = findViewById(R.id.notesEditText)

        mPictureButton.setOnClickListener { dispatchTakePictureIntent() }
        mSaveButton.setOnClickListener { writeNewReport() }

        mColorSpinner = findViewById(R.id.colorSpinner)
        mAmountSpinner = findViewById(R.id.amountSpinner)

        ArrayAdapter.createFromResource(
            this,
            R.array.colors_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            mColorSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.amounts_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            mAmountSpinner.adapter = adapter
        }
    }

    private fun writeNewReport() {
        // still need to get img
        val name = mNameEditText.text.toString()
        val color = mColorSpinner.selectedItem.toString()
        val amount = mAmountSpinner.selectedItem.toString()
        val notes = mNotesEditText.text.toString()

        val dbRef = database.getReference("reports").child(name).push()

        val report = Report(dbRef.key, name,color,amount,notes)
        dbRef.setValue(report)

        Toast.makeText(this, "Report Created", Toast.LENGTH_SHORT)
            .show()
        setResult(RESULT_OK)
        finish()
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            mImageView.setImageBitmap(imageBitmap)
        }
    }

    companion object {
        private const val TAG = "Invasive-Species"
        private const val REQUEST_IMAGE_CAPTURE = 1
        const val ADD_REPORT_REQUEST = 0
    }

}