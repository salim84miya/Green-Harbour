package com.example.greenharbour.Events

import android.content.Intent
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.greenharbour.MainActivity
import com.example.greenharbour.Models.BarrenLocation
import com.example.greenharbour.Models.Events
import com.example.greenharbour.R
import com.example.greenharbour.Utils.Geocoder
import com.example.greenharbour.databinding.ActivityBarrenLocationBinding
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class BarrenLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBarrenLocationBinding
    private var image_uri: Uri? = null
    private lateinit var latitude: String
    private lateinit var longitude: String
    private var address: String = "random"
    private lateinit var description: String
    private lateinit var eventTitle:String
    private lateinit var barrenLocation:BarrenLocation
    private val TAG = "autocomplete"

    private val selectImageIntent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            binding.eventsImg.setImageURI(uri)
            image_uri = uri
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarrenLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        barrenLocation = BarrenLocation()

        //adding event title
        binding.eventTitleBoxEditText.addTextChangedListener(object: TextWatcher {
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

                Geocoder.getLatLngFromAddress(this@BarrenLocationActivity,address){ lat, long->
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

    }

    //saving the data
    binding.saveBtn.setOnClickListener {
        saveAllData()
    }

    //validate all data
    private fun validateAll(): Boolean {
        return try {
            validateDescription(description)

        } catch (e: Exception) {
            Log.d("CreateEventActivity", e.localizedMessage.toString())
            Toast.makeText(
                this@BarrenLocationActivity,
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
    private fun saveAllData() {
        if (validateAll()) {

            binding.savingProgress.visibility = View.VISIBLE

            barrenLocation.location = lo
            event.eventContact = email
            event.eventDesc = description
            event.eventLocation = address
            event.eventDate = eventDate
            event.eventName = eventTitle
            event.eventTime = eventTime
            event.latitude = latitude
            event.longitude = longitude

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