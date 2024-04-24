package com.example.greenharbour.Events

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.greenharbour.Models.Events
import com.example.greenharbour.R
import com.example.greenharbour.databinding.ActivityEventDetailBinding
import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class EventDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventDetailBinding

    private lateinit var eventDesc: String
    private lateinit var eventImageUrl: String
    private lateinit var eventDate: String
    private lateinit var contact: String
    private lateinit var location: String
    private lateinit var eventTitle: String
    private lateinit var eventTime: String
    private var isParticipated = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getData()
        fetchParticipatedList()


binding.participateBtn.setOnClickListener {
    showingAlertBox()
}



    }

    private fun getData() {

//getting data from intent
        eventDesc = intent.getStringExtra("eventDesc").toString()
        eventImageUrl = intent.getStringExtra("eventImage").toString()
        eventDate = intent.getStringExtra("eventDate").toString()
        contact = intent.getStringExtra("contact").toString()
        location = intent.getStringExtra("location").toString()
        eventTitle = intent.getStringExtra("eventTitle").toString()
        eventTime = intent.getStringExtra("eventTime").toString()

        Picasso.get().load(eventImageUrl)
            .into(binding.eventImageView, object : Callback {
                override fun onSuccess() {
                    binding.progressBar.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    binding.progressBar.visibility = View.GONE
                }

            })

        binding.eventDescFr.text = eventDesc
        binding.eventLocationFr.text = location
        binding.contactFr.text = contact
        binding.evDateFr.text = eventDate
        binding.eventTitleFr.text = eventTitle
        binding.evTimeFr.text = eventTime

    }

    private fun participate() {



        val email = Firebase.auth.currentUser!!.email.toString()
        val data = hashMapOf(
            "email" to email
        )
         Firebase.firestore.collection(eventTitle).document(Firebase.auth.currentUser!!.uid).set(data).addOnSuccessListener {

                binding.participateBtn.text = "Participated"
                binding.participateBtn.visibility = View.VISIBLE

            val connection_string = "ParticipatedEvents"
            val participatedEvent = Events()
            participatedEvent.eventName = eventTitle
            participatedEvent.eventImageUrl = eventImageUrl
            participatedEvent.eventDesc = eventDesc
            participatedEvent.eventContact = contact
            participatedEvent.eventDate = eventDate
            participatedEvent.eventLocation = location

            Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + connection_string)
                .document(eventTitle).set(participatedEvent).addOnSuccessListener {

            }
        }
    }

    private fun showingAlertBox(){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Are you sure")
            builder.setMessage("Once you participate you can't undo this")
            builder.setPositiveButton("Yes",DialogInterface.OnClickListener { dialog, which ->
                participate()
            }
            )
            builder.setNegativeButton("Cancel",DialogInterface.OnClickListener { dialog, which ->

            })
            builder.show()
    }

    @SuppressLint("SetTextI18n")
    private fun fetchParticipatedList(){
        val email = Firebase.auth.currentUser!!.email.toString()
        Firebase.firestore.collection(eventTitle).get().addOnSuccessListener {

            for (document in it.documents){
                val participants = document.getString("email")
                if (participants==email){
                    isParticipated = true
                    break
                }
            }
            if(!isParticipated){
                binding.participateBtn.visibility = View.VISIBLE
            }else{
                binding.participateBtn.text = "Participated"
                binding.participateBtn.visibility = View.VISIBLE
            }
        }
    }

}