package com.example.greenharbour.Events

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.greenharbour.Models.Events
import com.example.greenharbour.R
import com.example.greenharbour.Utils.USER_EVENTS
import com.example.greenharbour.Utils.calculateDistance
import com.example.greenharbour.databinding.ActivityEventMapViewBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class EventMapViewActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityEventMapViewBinding
    private var lat1: Double = 0.00
    private var lang1: Double = 0.00
//    private lateinit var newLatLng :LatLng
//    private lateinit var eventsList: ArrayList<Events>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEventMapViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        Firebase.firestore.collection("Users").document(Firebase.auth.currentUser!!.uid).get()
//            .addOnSuccessListener {
//                if (it!=null && it.exists()){
//                    lat1 = it.get("latitude").toString().toDouble()
//                    lang1 = it.get("longitude").toString().toDouble()
//                    newLatLng = LatLng(lat1,lang1)
//                    fetchEventListData()
//                }
//
//            }
//        eventsList = ArrayList()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

//    private fun fetchEventListData() {
//        Firebase.firestore.collection(USER_EVENTS).get().addOnSuccessListener {
//            val tempEventList = ArrayList<Events>()
//            if (!it.isEmpty) {
//                for (i in it.documents) {
//                    val event = i.toObject<Events>()!!
//                    if (event.eventContact == Firebase.auth.currentUser!!.email.toString()) {
//                        continue
//                    }
//                    tempEventList.add(event)
//                }
//                eventsList.clear()
//                for (i in tempEventList) {
//                    if (calculateDistance(lat1,lang1,i.latitude!!.toDouble(),i.longitude!!.toDouble()) >10){
//                        continue
//                    }
//                    eventsList.add(i)
//                }
//
//            }
//
//        }
//    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,12.0f))
    }

//    private fun addMarker(googleMap: GoogleMap){
//        eventsList.forEach {
//            val latLng:LatLng = LatLng(it.latitude!!.toDouble(),it.longitude!!.toDouble())
//            val marker = googleMap.addMarker(
//                MarkerOptions()
//                    .title(it.eventName)
//                    .position(latLng)
//            )
//            Log.d("Markers","${it.eventName} and coordinates $latLng")
//        }
//    }
}