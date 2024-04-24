package com.example.greenharbour
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.greenharbour.Authorization.SignUpActivity
import com.example.greenharbour.databinding.ActivityUserAccountBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.io.IOException
import java.util.Locale


class UserAccountActivity : AppCompatActivity() {

    private lateinit var binding:ActivityUserAccountBinding
    private lateinit var username:String
    private lateinit var email:String
    private lateinit var location:String
    private var latitude:Double = 0.00
    private var longitude:Double = 0.00
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Firebase.firestore.collection("Users").document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                if (it!=null && it.exists()){
                    username = it.get("username").toString()
                    email = it.get("login").toString()
                    binding.emailValue.text = it.get("login").toString()
                    binding.usernameValue.text = it.get("username").toString()
                    latitude = it.get("latitude").toString().toDouble()
                    longitude = it.get("longitude").toString().toDouble()
                    getAddressFromLocation(latitude,longitude)
                }

            }

        binding.editProfile.setOnClickListener {
            val intent = Intent(this@UserAccountActivity,SignUpActivity::class.java)
            intent.putExtra("username",username)
            intent.putExtra("email",email)
            intent.putExtra("location",location)
            intent.putExtra("MODE",1)
            startActivity(intent)
        }

    }
    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            addresses?.let { addressList ->
                if (addressList.isNotEmpty()) {
                    val address = addressList[0]
                    val addressString = address.getAddressLine(0)
                    Log.d("Address", addressString)
                    // Do something with the address string
                    location = addressString
                    binding.locationValue.text = addressString
                } else {
                    Log.d("Address", "No address found")
                }
            } ?: Log.d("Address", "Address list is null")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("Address", "Error getting address from location")
        }
    }
}