package com.example.greenharbour.Events

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenharbour.Adapters.EventsViewAdapter
import com.example.greenharbour.Adapters.OnItemClickListener
import com.example.greenharbour.Models.Events
import com.example.greenharbour.databinding.ActivityMyCreatedEventsBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class MyCreatedEventsActivity : AppCompatActivity(),OnItemClickListener<Events> {
    private lateinit var binding: ActivityMyCreatedEventsBinding
    private lateinit var eventsList: ArrayList<Events>
    private lateinit var eventsViewAdapter: EventsViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyCreatedEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventsList = ArrayList<Events>()
        eventsViewAdapter = EventsViewAdapter(this@MyCreatedEventsActivity, eventsList,this@MyCreatedEventsActivity)
        binding.eventsRecyclerView.layoutManager = LinearLayoutManager(this@MyCreatedEventsActivity)
        binding.eventsRecyclerView.adapter = eventsViewAdapter
        fetchEventListData()


    }

    private fun fetchEventListData() {
        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
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

         val intent =   Intent(this@MyCreatedEventsActivity,MyEventActivity::class.java)
        intent.putExtra("eventDesc",eventDesc)
        intent.putExtra("eventImage",eventImgUrl)
        intent.putExtra("eventDate",eventDate)
        intent.putExtra("contact",contact)
        intent.putExtra("location",location)
        intent.putExtra("eventTitle",eventTitle)
        startActivity(intent)

//        Toast.makeText(this, "Event Desc: ${eventDesc} Event Date: ${eventDate} Contact :${contact} Location :${location}", Toast.LENGTH_SHORT).show()
    }
}