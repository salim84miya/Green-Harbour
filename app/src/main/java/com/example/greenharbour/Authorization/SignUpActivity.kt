package com.example.greenharbour.Authorization


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.greenharbour.MainActivity
import com.example.greenharbour.Models.User
import com.example.greenharbour.R
import com.example.greenharbour.Utils.Geocoder
import com.example.greenharbour.databinding.ActivitySignUpBinding
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var username: String
    private lateinit var login: String
    private lateinit var password: String
    private val TAG = "Autocomplete"
    private lateinit var address:String
    private lateinit var latitude:String
    private lateinit var longitude:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

//        Glide.with(this).load(R.raw.leaves_flow).into(binding.leavesFlowImg)

        val text =
            "<font color=#808080>Already have an account ? </font> <font color=#069563>Login In</font>"
        binding.LoginText.setText(Html.fromHtml(text))

        //handling the username validation
        binding.username.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                val newText = s?.toString() ?: ""
                validateUsername(newText)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        //handling login validation
        binding.Login.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(text: Editable?) {
                validateLogin(text.toString())
            }
        })

        //handling password validation
        binding.password.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val pass = s?.toString() ?: ""
                validatePassword(pass)
            }

            override fun afterTextChanged(pass: Editable?) {}
        })

        binding.signUpBtn.setOnClickListener {
            username = binding.username.editText?.text.toString()
            login = binding.Login.editText?.text.toString()
            password = binding.password.editText?.text.toString()

            val user = User(username,login,address,latitude,longitude)
            if (validateFields()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(login, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Firebase.firestore.collection("Users").document(Firebase.auth.currentUser!!.uid.toString()).set(user).addOnCompleteListener {
                                if(it.isSuccessful){
                                    startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                                    finish()
                                }
                            }

                        } else {
                            Toast.makeText(
                                this@SignUpActivity,
                                task.exception?.localizedMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }

        binding.LoginText.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
        }

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

            Geocoder.getLatLngFromAddress(this@SignUpActivity,address){lat,long->
                Log.i(TAG, "Latitude: ${lat},Longitude $long")
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

    private fun validateFields(): Boolean {
        return validateUsername(username) && validateLogin(login) && validatePassword(password)
    }

    private fun validateUsername(s: String): Boolean {
        return when {
            s.isEmpty() -> {
                binding.username.error = "username cannot be empty"
                false
            }
            s.length < 6 -> {
                binding.username.error = "username must be 6 characters long"
                false
            }
            else -> {
                binding.username.error = null
                true
            }
        }
    }

    private fun validateLogin(s: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$")
        return when {
            s.isEmpty() -> {
                binding.Login.error = "login cannot be empty"
                false
            }
            !emailRegex.matches(s) -> {
                binding.Login.error = "Invalid email format"
                false
            }
            else -> {
                binding.Login.error = null
                true
            }
        }
    }

    private fun validatePassword(password: String): Boolean {
        val passwordRegex = Regex(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+-=:;'\"\\|/?,.\\s<])(?=\\S+\$).{8,}\$"
        )
        return if (passwordRegex.matches(password)) {
            binding.password.error = null
            true
        } else {
            binding.password.error =
                "Password must be at least 8 characters and contain at least one uppercase letter, one lowercase letter, one digit, and one special character."
            false
        }
    }

}
