package com.example.greenharbour.Events

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenharbour.Adapters.BarrenEventViewAdapter
import com.example.greenharbour.Adapters.OnItemClickListener
import com.example.greenharbour.Models.BarrenLocation
import com.example.greenharbour.databinding.ActivityNearbyBarrenLocationBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class NearbyBarrenLocation : AppCompatActivity(), OnItemClickListener<BarrenLocation> {

    private lateinit var binding: ActivityNearbyBarrenLocationBinding
    private lateinit var eventsList: ArrayList<BarrenLocation>
    private lateinit var eventsViewAdapter: BarrenEventViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNearbyBarrenLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventsList = ArrayList<BarrenLocation>()
        eventsViewAdapter =
            BarrenEventViewAdapter(this@NearbyBarrenLocation, eventsList, this@NearbyBarrenLocation)
        binding.rv.layoutManager = LinearLayoutManager(this@NearbyBarrenLocation)
        binding.rv.adapter = eventsViewAdapter
        fetchEventListData()

    }

    private fun fetchEventListData() {
        Firebase.firestore.collection("Barren event " + Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                val tempEventList = ArrayList<BarrenLocation>()
                if (!it.isEmpty) {
                    for (i in it.documents) {
                        val event = i.toObject<BarrenLocation>()!!
                        tempEventList.add(event)
                    }
                    eventsList.clear()
                    eventsList.addAll(tempEventList)

                    for (i in eventsList) {
                        Log.d("NearbyEventActivity", "image${i.img}")
                    }

                    eventsViewAdapter.notifyDataSetChanged()


                }

            }
    }

    override fun onItemClick(view: BarrenLocation) {
        val eventDesc = view.description
        val location = view.location
        val eventImgUrl = view.img
        val eventTitle = view.title

        val intent =   Intent(this@NearbyBarrenLocation,BarrenEventDetailActivity ::class.java)
        intent.putExtra("eventDesc",eventDesc)
        intent.putExtra("eventImage",eventImgUrl)
        intent.putExtra("location",location)
        intent.putExtra("eventTitle",eventTitle)
        intent.putExtra("MODE",1)
        startActivity(intent)
    }
}