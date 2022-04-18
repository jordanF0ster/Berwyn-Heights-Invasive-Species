package com.example.invasivespecies

import android.R.attr.button
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


private lateinit var mImageView : ImageView
private lateinit var mPictureButton: Button
private lateinit var mSaveButton: Button
private lateinit var mNameEditText: EditText
private lateinit var mNotesEditText: EditText
private lateinit var mColorSpinner: Spinner
private lateinit var mAmountSpinner: Spinner

class ReportingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporting)

        mImageView = findViewById(R.id.imageView)
        mPictureButton = findViewById(R.id.pictureButton)
        mSaveButton = findViewById(R.id.saveButton)
        mNameEditText = findViewById(R.id.textPlantName)
        mNotesEditText = findViewById(R.id.notesEditText)

        mPictureButton.setOnClickListener { dispatchTakePictureIntent() }
        mSaveButton.setOnClickListener { saveInfo() }

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

    private fun saveInfo() {
        val name = mNameEditText.text.toString()
        val color = mColorSpinner.selectedItem.toString()
        val amount = mAmountSpinner.selectedItem.toString()
        val notes = mNotesEditText.text.toString()

        Toast.makeText(this, "$name $color $amount $notes", Toast.LENGTH_SHORT)
            .show()
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
    }

}