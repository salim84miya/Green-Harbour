package com.example.greenharbour.Events

import android.content.Intent
import android.icu.util.Calendar
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
import com.example.greenharbour.MainActivity
import com.example.greenharbour.Models.Events
import com.example.greenharbour.R
import com.example.greenharbour.Utils.Geocoder
import com.example.greenharbour.databinding.ActivityCreateEventsBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CreateEventsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateEventsBinding
    private var image_uri: Uri? = null
    private lateinit var latitude: String
    private lateinit var longitude: String
    private var address: String = "random"
    private lateinit var description: String
    private lateinit var email: String
    private lateinit var eventTitle:String
    private lateinit var event: Events
    private lateinit var eventDate: String
    private lateinit var eventTime:String
    private val USER_EVENTS: String = "USER_EVENTS"
    private val TAG = "autocomplete"

    private val selectImageIntent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
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



        //adding event title
        binding.eventTitleBoxEditText.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                eventTitle = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        eventTitle = binding.eventTitleBoxEditText.text.toString()


        //upload image from gallery
        binding.uploadImgGallery.setOnClickListener {
            selectImageIntent.launch("image/*")
        }

        //fetch date
        binding.eventDateEditText.setOnClickListener {
            openDatePicker()
        }
        binding.eventDate.setOnClickListener {
            openDatePicker()
        }

        //fetch time
        binding.eventTime.setOnClickListener {
            openTimePicker()
        }
        binding.eventTimeEditText.setOnClickListener {
            openTimePicker()
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

        //getting location
        val apiKey = getString(R.string.ApiKey)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext,apiKey );
        }
        val placesClient = Places.createClient(this)



        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: ${place.name}")
                address = place.name.toString()

                Geocoder.getLatLngFromAddress(this@CreateEventsActivity,address){lat,long->
                    Log.d(TAG,"Latitude:${lat},Longitude $long")
                    latitude = lat.toString()
                    longitude = long.toString()
                }

            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: $status")
            }
        })

        if (intent.getIntExtra("MODE",-1)==1){
            binding.eventTitleBoxEditText.setText(intent.getStringExtra("eventTitle").toString())
            binding.eventDescriptionBoxEditText.setText(intent.getStringExtra("eventDesc").toString())
            autocompleteFragment.setText(intent.getStringExtra("location").toString())
        }

        if(intent.getIntExtra("MODE",-1)==2){
            val eventDesc =    intent.getStringExtra("eventDesc")
            val eventImageUrl =    intent.getStringExtra("eventImage")
            val eventDate =    intent.getStringExtra("eventDate")
            val contact =    intent.getStringExtra("eventContact")
            val location =    intent.getStringExtra("eventLocation")
            val eventTitle = intent.getStringExtra("eventTitle")
            val eventTime = intent.getStringExtra("eventTime")

            binding.eventTitleBoxEditText.setText(eventTitle.toString())
            binding.eventDescriptionBoxEditText.setText(eventDesc.toString())
            autocompleteFragment.setText(location.toString())
            binding.eventTimeEditText.setText(eventTime.toString())
            binding.contactDetailsEditText.setText(contact.toString())
            binding.eventDateEditText.setText(eventDate.toString())
            Picasso.get().load(eventImageUrl)
                .into(binding.eventsImg)
        }

        //saving the data
        binding.saveBtn.setOnClickListener {
            saveAllData()
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

    private fun openTimePicker(){
        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(10)
                .setTitleText("Select Appointment time")
                .build()

        picker.show(supportFragmentManager, "tag")

        picker.addOnPositiveButtonClickListener {
            val selectedHour = if (picker.hour < 10) "0${picker.hour}" else "${picker.hour}"
            val selectedMinute = if (picker.minute < 10) "0${picker.minute}" else "${picker.minute}"
            val selectedTime = "$selectedHour:$selectedMinute"
            eventTime = selectedTime
            binding.eventTimeEditText.setText(selectedTime)
        }
    }

    //validate all data
    private fun validateAll(): Boolean {
        return try {
            validateDescription(description) && validateContactEmail(email)

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


    private fun saveAllData() {
        if (validateAll()) {


            binding.savingProgress.visibility = View.VISIBLE

            event.eventContact = email
            event.eventDesc = description
            event.eventLocation = address
            event.eventDate = eventDate
            event.eventName = eventTitle
            event.eventTime = eventTime
            event.latitude = latitude
            event.longitude = longitude

            try {
                if (intent.getIntExtra("MODE",-1)==2){

                    FirebaseStorage.getInstance().getReference("images")
                        .child(System.currentTimeMillis().toString()).putFile(image_uri!!)
                        .addOnSuccessListener {
                            it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                                val updates = hashMapOf<String, Any>(
                                    "eventContact" to email,
                                    "eventDate" to eventDate,
                                    "eventDesc" to description,
                                    "eventLocation" to address,
                                    "eventName" to eventTitle,
                                    "eventTime" to eventTime,
                                    "eventImageUrl" to it.toString()
                                )

                                Firebase.firestore.collection(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .document(eventTitle).update(updates)
                                    .addOnSuccessListener {
                                        binding.savingProgress.visibility = View.GONE
                                        Toast.makeText(
                                            this@CreateEventsActivity,
                                            "event updated successfully",
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

                }

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