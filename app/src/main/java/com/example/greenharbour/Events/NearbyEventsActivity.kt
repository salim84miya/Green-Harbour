package com.example.greenharbour.Events

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenharbour.Adapters.EventsViewAdapter
import com.example.greenharbour.Models.Events
import com.example.greenharbour.R
import com.example.greenharbour.Utils.USER_EVENTS
import com.example.greenharbour.databinding.ActivityNearbyEventsBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.storage

class NearbyEventsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNearbyEventsBinding
    private lateinit var eventsList: ArrayList<Events>
    private lateinit var eventsViewAdapter: EventsViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNearbyEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventsList = ArrayList()

        fetchEventListData()


    }

    private fun fetchEventListData() {
        Firebase.firestore.collection(USER_EVENTS).get().addOnSuccessListener {
            val tempEventList = ArrayList<Events>()
            if (!it.isEmpty) {
                for (i in it.documents) {
                    val event = i.toObject<Events>()!!
                    tempEventList.add(event)
                }
                eventsList.clear()
                eventsList.addAll(tempEventList)

                for(i in eventsList){
                    Log.d("NearbyEventActivity","image${i.eventImageUrl}")
                }

                eventsViewAdapter = EventsViewAdapter(this@NearbyEventsActivity, eventsList)
                binding.eventsRecyclerView.layoutManager = LinearLayoutManager(this@NearbyEventsActivity)
                binding.eventsRecyclerView.adapter = eventsViewAdapter

                eventsViewAdapter.notifyDataSetChanged()



            }

        }
    }
}