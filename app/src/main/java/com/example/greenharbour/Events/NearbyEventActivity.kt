package com.example.greenharbour.Events

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenharbour.Adapters.EventsViewAdapter
import com.example.greenharbour.Adapters.OnItemClickListener
import com.example.greenharbour.Models.Events
import com.example.greenharbour.Utils.USER_EVENTS
import com.example.greenharbour.databinding.ActivityNearbyEventBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class NearbyEventActivity : AppCompatActivity(), OnItemClickListener<Events> {

    private lateinit var binding:ActivityNearbyEventBinding
    private lateinit var eventsList: ArrayList<Events>
    private lateinit var eventsViewAdapter: EventsViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNearbyEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventsList = ArrayList()
        eventsViewAdapter = EventsViewAdapter(this@NearbyEventActivity, eventsList,this@NearbyEventActivity)
        binding.eventsRecyclerView.layoutManager = LinearLayoutManager(this@NearbyEventActivity)
        binding.eventsRecyclerView.adapter = eventsViewAdapter
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

                eventsViewAdapter.notifyDataSetChanged()
            }

        }
    }

    override fun onItemClick(view: Events) {
        val eventDesc = view.eventDesc
        val eventDate = view.eventDate
        val contact = view.eventContact
        val location = view.eventLocation
        val eventImgUrl = view.eventImageUrl
        val eventTitle = view.eventName

        val intent =   Intent(this@NearbyEventActivity,EventDetailActivity::class.java)
        intent.putExtra("eventDesc",eventDesc)
        intent.putExtra("eventImage",eventImgUrl)
        intent.putExtra("eventDate",eventDate)
        intent.putExtra("contact",contact)
        intent.putExtra("location",location)
        intent.putExtra("eventTitle",eventTitle)
        startActivity(intent)

    }
}