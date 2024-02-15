package com.example.greenharbour.Events

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.greenharbour.R
import com.example.greenharbour.databinding.ActivityCreateEventsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.io.FileDescriptor
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateEventsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateEventsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var image_uri: Uri? = null
    private val RESULT_LOAD_IMAGE = 123
    val IMAGE_CAPTURE_CODE = 654
    private val REQUEST_CODE = 102
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var address: String = "random"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //check for permission of camera and external storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED
            ) {
                val permission =
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, 112)
            }
        }

        //camera upload button check for permission and then open camera
        binding.uploadImgBtn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_DENIED
                ) {
                    val permission = arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    requestPermissions(permission, 112)
                } else {
                    openCamera()
                }
            } else {

            }
            true
        }

        //upload image from gallery
        binding.uploadImgGallery.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE)
        }

        //fetch current location
        binding.fetchLocationBtn.setOnClickListener {
            fetchLocation()
        }

        //fetch date
        binding.eventDateEditText.setOnClickListener {

            val tomorrowInMilliseconds = MaterialDatePicker.todayInUtcMilliseconds() + 86400000

            val constraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.from(tomorrowInMilliseconds))

            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setSelection(tomorrowInMilliseconds)
                    .setCalendarConstraints(constraintsBuilder.build())
                    .build()

            datePicker.show(supportFragmentManager,"tag")

            datePicker.addOnPositiveButtonClickListener {selection->
                val date:String = SimpleDateFormat("dd-MM-yyyy",Locale.getDefault()).format(Date(selection))
                binding.eventDateEditText.setText(date)

            }
        }

        //adding contact details
        binding.contactDetailsEditText.setText(Firebase.auth.currentUser?.email)


    }

    //open camera for taking image
    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)

    }

    //set image from camera and gallery to events imageView
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == Activity.RESULT_OK) {
            val bitmapCamera = uriToBitmap(image_uri!!)
            binding.eventsImg.setImageBitmap(bitmapCamera)
        }

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            image_uri = data.data
            val bitmapGallery = uriToBitmap(image_uri!!)
            binding.eventsImg.setImageBitmap(bitmapGallery)
        }
    }

    //TODO takes URI of the image and returns bitmap
    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    //fetch current location
    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    // Get address using Geocoder
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses: MutableList<Address>? =
                        geocoder.getFromLocation(latitude, longitude, 1)
                    if (addresses?.isNotEmpty() == true) {
                        val address = addresses[0].getAddressLine(0)
                        displayLocationDetails(latitude, longitude, address)
                        Toast.makeText(
                            this@CreateEventsActivity,
                            "got location Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.e("CreateEventActivity", "Geocoder failed to get address")
                        // Handle address retrieval failure
                    }
                } else {
                    Log.d("CreateEventActivity", "Location is null")
                    // Handle location unavailable error
                }
            }
            .addOnFailureListener { e ->
                Log.e("CreateEventActivity", "Error fetching location: $e")
                // Handle location retrieval failure
            }
    }

    private fun displayLocationDetails(latitude: Double, longitude: Double, address: String) {

        this.latitude = latitude
        this.longitude = longitude
        this.address = address

        val eventLocationEditText =
            binding.locationBox.editText?.findViewById<TextInputEditText>(R.id.locationEditText)

        eventLocationEditText?.setText(address)
        binding.locationBox.visibility = View.VISIBLE
    }

}