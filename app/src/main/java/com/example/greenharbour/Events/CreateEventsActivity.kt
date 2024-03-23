package com.example.greenharbour.Events

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.greenharbour.MainActivity
import com.example.greenharbour.Models.Events
import com.example.greenharbour.R
import com.example.greenharbour.databinding.ActivityCreateEventsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CreateEventsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateEventsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var image_uri: Uri? = null
    private val REQUEST_CODE = 102
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var address: String = "random"
    private lateinit var description: String
    private lateinit var email: String
    private lateinit var location: String
    private lateinit var eventTitle:String
    private var gotLocation: Boolean = false
    private lateinit var event: Events
    private lateinit var eventDate: String
    private val USER_EVENTS: String = "USER_EVENTS"

    val selectImageIntent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            binding.eventsImg.setImageURI(uri)
            image_uri = uri
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEventsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        event = Events()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //adding event title
        eventTitle = binding.eventTitleBoxEditText.text.toString()


        //upload image from gallery
        binding.uploadImgGallery.setOnClickListener {
            selectImageIntent.launch("image/*")
        }

        //fetch current location
        binding.fetchLocationBtn.setOnClickListener {
            fetchLocation()
            location = binding.locationEditText.text.toString()
        }


        //fetch date
        binding.eventDateEditText.setOnClickListener {
            openDatePicker()
        }
        binding.eventDate.setOnClickListener {
            openDatePicker()
        }


        //adding contact details
        email = Firebase.auth.currentUser?.email.toString()
        binding.contactDetailsEditText.setText(email)

        val wordCountLimit = 250

        //adding validation for description box
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                description = s.toString()
                val wordCount = s?.toString()?.trim()?.split(" ")?.count() ?: 0
                if (wordCount > 250) {
                    val trimmedText = s.toString().trim()
                        .substring(0, s.toString().trim().lastIndexOf(' ', 250 * 5 - 1) + 1) + "..."
                    binding.eventDescriptionBoxEditText.setText(trimmedText)
                    binding.eventDescriptionBoxEditText.setSelection(trimmedText.length)
                    binding.eventDescriptionBox.helperText = "Word limit exceeded."
                } else {
                    binding.eventDescriptionBox.helperText = "Words: $wordCount / 250"
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }
        }


        binding.eventDescriptionBoxEditText.addTextChangedListener(textWatcher)

        binding.contactDetailsEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(contactEmail: CharSequence?, p1: Int, p2: Int, p3: Int) {
                email = contactEmail.toString()
                validateContactEmail(email)
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })


        //saving the data
        binding.saveBtn.setOnClickListener {
            saveAllData()
        }

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
                        gotLocation = true
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

    private fun openDatePicker() {

        val tomorrowInMilliseconds = MaterialDatePicker.todayInUtcMilliseconds() + 86400000

        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.from(tomorrowInMilliseconds))

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(tomorrowInMilliseconds)
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

        datePicker.show(supportFragmentManager, "tag")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val date: String =
                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(selection))
            binding.eventDateEditText.setText(date)

        }

        binding.eventDateEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

                val dateString = s.toString()
                if (dateString.isNotEmpty()) {
                    val selectedDate =
                        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(dateString)
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.MONTH, 6)
                    val maxDate = calendar.time

                    if (selectedDate != null && selectedDate.after(maxDate)) {
                        binding.eventDate.error = "Date must be within 6 months from today"
                    } else {
                        binding.eventDate.error = null
                        eventDate = dateString
                    }
                } else {
                    binding.eventDate.error = "Date cannot be empty"
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
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

    //validate all data
    private fun validateAll(): Boolean {
        return try {
            validateDescription(description) && validateContactEmail(email) && validateLocation(
                location
            )

        } catch (e: Exception) {
            Log.d("CreateEventActivity", e.localizedMessage.toString())
            Toast.makeText(
                this@CreateEventsActivity,
                "please click on get location",
                Toast.LENGTH_SHORT
            ).show()
            false
        }
    }

    //validateDescriptionBox content
    private fun validateDescription(s: String): Boolean {
        return when {
            s.isEmpty() -> {
                binding.eventDescriptionBox.error = "description cannot be empty"
                false
            }

            s.length < 50 -> {
                binding.eventDescriptionBox.error = "description must be at least 50 words long"
                false
            }

            else -> {
                binding.eventDescriptionBox.error = null
                true
            }
        }
    }

    //validating contact email
    private fun validateContactEmail(s: String): Boolean {

        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$")
        return when {
            s.isEmpty() -> {
                binding.contactDetails.error = "contact cannot be empty"
                false
            }

            !emailRegex.matches(s) -> {
                binding.contactDetails.error = "Invalid email format"
                false
            }

            else -> {
                binding.contactDetails.error = null
                true
            }
        }
    }

    //validate location
    private fun validateLocation(address: String): Boolean {
        return if (gotLocation) {
            binding.locationBox.error = null // Clear any previous error message

            true
        } else {
            binding.locationBox.error = "Please enter a valid address"
            false
        }
    }

    private fun saveAllData() {
        if (validateAll()) {

            binding.savingProgress.visibility = View.VISIBLE

            event.eventContact = email
            event.eventDesc = description
            event.eventLocation = address
            event.eventDate = eventDate
            event.eventName = eventTitle

            try {
                FirebaseStorage.getInstance().getReference("images")
                    .child(System.currentTimeMillis().toString()).putFile(image_uri!!)
                    .addOnSuccessListener {
                        it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                            event.eventImageUrl = it.toString()

                            val uid = UUID.randomUUID().toString()

                            Firebase.firestore.collection(USER_EVENTS)
                                .document(uid).set(event)
                                .addOnSuccessListener {
                                }

                            Firebase.firestore.collection(FirebaseAuth.getInstance().currentUser!!.uid)
                                .document(uid).set(event)
                                .addOnSuccessListener {
                                    binding.savingProgress.visibility = View.GONE
                                    Toast.makeText(
                                        this@CreateEventsActivity,
                                        "event created successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(
                                        Intent(
                                            this@CreateEventsActivity,
                                            MainActivity::class.java
                                        )
                                    )
                                    finish()
                                }
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            Toast.makeText(
                this@CreateEventsActivity,
                "please enter all the details properly",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}