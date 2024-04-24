package com.example.greenharbour.Events

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.greenharbour.Adapters.MyEventViewAdapter
import com.example.greenharbour.EventFragments.EventParticipationFragment
import com.example.greenharbour.EventFragments.EventViewFragment
import com.example.greenharbour.Models.Events
import com.example.greenharbour.ViewModel.EventVIewModel
import com.example.greenharbour.databinding.ActivityMyEventBinding
import com.google.android.material.tabs.TabLayoutMediator

class MyEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyEventBinding
    private lateinit var viewModel: EventVIewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(EventVIewModel::class.java)

//        setSupportActionBar(binding.materialToolbar2)
        tabLayoutWithViewPager()

        getData()

    }

    private fun getData(){

//getting data from intent
        val eventDesc =    intent.getStringExtra("eventDesc")
        val eventImageUrl =    intent.getStringExtra("eventImage")
        val eventDate =    intent.getStringExtra("eventDate")
        val contact =    intent.getStringExtra("contact")
        val location =    intent.getStringExtra("location")
        val eventTitle = intent.getStringExtra("eventTitle")
        val eventTime = intent.getStringExtra("eventTime")

        val event = Events()
        event.eventDesc = eventDesc
        event.eventLocation = location
        event.eventContact = contact
        event.eventDate = eventDate
        event.eventImageUrl = eventImageUrl
        event.eventName = eventTitle
        event.eventTime = eventTime

//        setting data into viewModel
        viewModel.setData(event)
    }

    private fun tabLayoutWithViewPager(){
        val fragments = ArrayList<Fragment>()
        fragments.add(EventViewFragment())
        fragments.add(EventParticipationFragment())

        val adapter = MyEventViewAdapter(fragments,supportFragmentManager,lifecycle)
        binding.viewPager.adapter = adapter


        TabLayoutMediator(binding.tabLayout,binding.viewPager){tab,position->

            tab.text = when(position){
                0->"Events"
                1->"Participants"
                else -> "Tab $position"
            }


        }.attach()
    }
}