package com.example.invasivespecies

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.os.StrictMode
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


private lateinit var mImageView : ImageView
private lateinit var mPictureButton: Button
private lateinit var mSaveButton: Button
private lateinit var mNameEditText: TextView
private lateinit var mNotesEditText: EditText
private lateinit var mColorSpinner: Spinner
private lateinit var mAmountSpinner: Spinner

private var mIsRequestingUpdates = false
private lateinit var database: FirebaseDatabase
private lateinit var storageRef: StorageReference
private lateinit var mFusedLocationClient: FusedLocationProviderClient
private lateinit var mLocationRequest: LocationRequest
private lateinit var mLocationCallback: LocationCallback
private var deviceLocation: Location? = null

class ReportingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporting)

        // Allow for drawable building in main thread
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)

        database = FirebaseDatabase.getInstance()
        storageRef = Firebase.storage.reference
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                FINE_LOC_PERM_REQ
            )
        } else {
            lastKnownLocation()
        }

        mImageView = findViewById(R.id.imageView)
        mPictureButton = findViewById(R.id.pictureButton)
        mSaveButton = findViewById(R.id.saveButton)
        mNameEditText = findViewById(R.id.textPlantName)
        mNotesEditText = findViewById(R.id.notesEditText)

        mPictureButton.setOnClickListener { dispatchTakePictureIntent() }
        mSaveButton.setOnClickListener { writeNewReport() }

        mColorSpinner = findViewById(R.id.colorSpinner)
        mAmountSpinner = findViewById(R.id.amountSpinner)

        // Set data
        mNameEditText.text = intent.getStringExtra("selection name")

        val uri = intent.getStringExtra("selection image uri")
        val d: Drawable = Drawable.createFromStream(URL(uri).content as InputStream, "src name")

        mImageView.setImageDrawable(d)


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
        val name = mNameEditText.text.toString().lowercase()
        val color = mColorSpinner.selectedItem.toString()
        val amount = mAmountSpinner.selectedItem.toString()
        val notes = mNotesEditText.text.toString()
        val user = Firebase.auth.currentUser
        val creator = if (user != null && user.displayName != null) user.displayName else "none"

        val dbRef = database.getReference("reports").child(name).push()

        val imageRef = storageRef.child(dbRef.key + ".jpg")

        mImageView.isDrawingCacheEnabled = true
        mImageView.buildDrawingCache()
        if (mImageView.drawable != null) {
            val bitmap = (mImageView.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            var uploadTask = imageRef.putBytes(data)
            uploadTask.addOnFailureListener {
                Toast.makeText(this, "Failed to upload Image", Toast.LENGTH_SHORT)
                Log.i(TAG, "failed to upload imageView: " + it.message)
                setResult(RESULT_OK)
                finish()
            }
        }

        val report = Report(
            dbRef.key,
            name,
            color,
            amount,
            notes,
            deviceLocation,
            creator!!
        )
        try {
            dbRef.setValue(report)
        } catch (e: java.lang.Exception) {
            Log.i(TAG, "Failed to save to db: $e")
        }

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
    // everything below is from / based off of LocationGetLocationActivity.kt
    // from LocationGetLocationServices in the class example repo
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (FINE_LOC_PERM_REQ == requestCode &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            lastKnownLocation()
        } else {
            Toast.makeText(
                this,
                "This app requires ACCESS_FINE_LOCATION permission",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun lastKnownLocation() {
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.lastLocation
                .addOnSuccessListener(this) { location: Location? ->
                    installLocationListeners()
                }
        }
    }

    private fun installLocationListeners() { // Request location services
        mLocationRequest = LocationRequest.create().apply {
            interval = POLLING_FREQ
            fastestInterval = FASTEST_UPDATE_FREQ
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest!!)

        // Used if needed to turn on settings related to location services
        val client = LocationServices.getSettingsClient(this)
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener(this) {
                // All location settings are satisfied. The client can initialize location request.
                finishInstallLocationListeners()
            }
            .addOnFailureListener(this) { e: Exception ->
                when ((e as ApiException).statusCode) {
                    CommonStatusCodes.RESOLUTION_REQUIRED ->
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            val resolvable = e as ResolvableApiException
                            resolvable.startResolutionForResult(
                                this,
                                REQUEST_CHECK_SETTINGS
                            )
                        } catch (sendEx: IntentSender.SendIntentException) {
                            // Ignore the error.
                        }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
    }

    private fun finishInstallLocationListeners() {
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationCallback = getLocationCallback()
            if (!mIsRequestingUpdates) {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest!!, mLocationCallback,
                    Looper.getMainLooper()
                )
                mIsRequestingUpdates = true
            }
            // Schedule a runnable to stop location updates after a period of time
            val mCancelHandle = Executors.newScheduledThreadPool(1).schedule(
                {
                    if (mIsRequestingUpdates) {
                        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                        mIsRequestingUpdates = false
                    }
                },
                MEASURE_TIME,
                TimeUnit.MILLISECONDS
            )
        }
    }

    private fun getLocationCallback(): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                // Get new location
                val location = locationResult.lastLocation

                // Determine whether new location is better than current best estimate
                if (null == deviceLocation ||
                    location.accuracy <= deviceLocation!!.accuracy ||
                    location.distanceTo(deviceLocation) > MIN_DIST_READ
                ) {

                    // Update best location
                    deviceLocation = location

                    // Turn off location updates if location reading is sufficiently accurate
                    if (deviceLocation!!.accuracy < MIN_ACCURACY) {
                        if (mIsRequestingUpdates) {
                            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                            mIsRequestingUpdates = false
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "Invasive-Species"
        private const val REQUEST_IMAGE_CAPTURE = 1
        const val ADD_REPORT_REQUEST = 0
        const val VIEW_REPORT_REQUEST = 1
        private const val FINE_LOC_PERM_REQ = 200
        private const val POLLING_FREQ = 1000 * 10.toLong()
        private const val FASTEST_UPDATE_FREQ = 1000 * 2.toLong()
        private const val REQUEST_CHECK_SETTINGS = 201
        private const val ONE_MIN = 1000 * 60.toLong()
        private const val MEASURE_TIME = ONE_MIN
        private const val MIN_ACCURACY = 5.0f
        private const val MIN_DIST_READ = 200.0f
    }

}