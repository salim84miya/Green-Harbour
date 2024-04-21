package com.example.greenharbour.Events

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenharbour.Adapters.EventsViewAdapter
import com.example.greenharbour.Adapters.OnItemClickListener
import com.example.greenharbour.Models.Events
import com.example.greenharbour.Models.User
import com.example.greenharbour.Utils.USER_EVENTS
import com.example.greenharbour.Utils.calculateDistance
import com.example.greenharbour.databinding.ActivityNearbyEventBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class NearbyEventActivity : AppCompatActivity(), OnItemClickListener<Events> {

    private lateinit var binding: ActivityNearbyEventBinding
    private lateinit var eventsList: ArrayList<Events>
    private lateinit var eventsViewAdapter: EventsViewAdapter
    private var lat1: Double = 0.00
    private var lang1: Double = 0.00
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNearbyEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Firebase.firestore.collection("Users").document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                if (it!=null && it.exists()){
                    lat1 = it.get("latitude").toString().toDouble()
                    lang1 = it.get("longitude").toString().toDouble()

                }

            }
        eventsList = ArrayList()

        eventsViewAdapter =
            EventsViewAdapter(this@NearbyEventActivity, eventsList, this@NearbyEventActivity)
        binding.eventsRecyclerView.layoutManager = LinearLayoutManager(this@NearbyEventActivity)
        binding.eventsRecyclerView.adapter = eventsViewAdapter
        fetchEventListData()


        //starting an map for map representation
//        binding.floatingActionButton.setOnClickListener {
//            startActivity(Intent(this@NearbyEventActivity,EventMapViewActivity::class.java))
//        }
    }

    private fun fetchEventListData() {
        Firebase.firestore.collection(USER_EVENTS).get().addOnSuccessListener {
            val tempEventList = ArrayList<Events>()
            if (!it.isEmpty) {
                for (i in it.documents) {
                    val event = i.toObject<Events>()!!
                    if (event.eventContact == Firebase.auth.currentUser!!.email.toString()) {
                        continue
                    }
                        tempEventList.add(event)
                }
                eventsList.clear()
                for (i in tempEventList) {
                    if (calculateDistance(lat1,lang1,i.latitude!!.toDouble(),i.longitude!!.toDouble())>10){
                        continue
                    }
                 eventsList.add(i)
                }


                for (i in eventsList) {
                    Log.d("NearbyEventActivity", "image${i.eventImageUrl}")
                }

                eventsViewAdapter = EventsViewAdapter(
                    this@NearbyEventActivity,
                    eventsList,
                    this@NearbyEventActivity
                )
                binding.eventsRecyclerView.layoutManager =
                    LinearLayoutManager(this@NearbyEventActivity)
                binding.eventsRecyclerView.adapter = eventsViewAdapter

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

        val intent = Intent(this@NearbyEventActivity, EventDetailActivity::class.java)
        intent.putExtra("eventDesc", eventDesc)
        intent.putExtra("eventImage", eventImgUrl)
        intent.putExtra("eventDate", eventDate)
        intent.putExtra("contact", contact)
        intent.putExtra("location", location)
        intent.putExtra("eventTitle", eventTitle)
        startActivity(intent)

    }
}