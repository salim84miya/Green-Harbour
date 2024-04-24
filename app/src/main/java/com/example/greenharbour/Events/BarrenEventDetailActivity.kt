package com.example.greenharbour.Events

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.greenharbour.R
import com.example.greenharbour.databinding.ActivityBarrenEventDetailBinding
import com.example.greenharbour.databinding.ActivityEventDetailBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class BarrenEventDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBarrenEventDetailBinding

    private lateinit var eventDesc: String
    private lateinit var eventImageUrl: String
    private lateinit var location: String
    private lateinit var eventTitle: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBarrenEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.getIntExtra("MODE",-1)==1){
            binding.createEvent.visibility = View.VISIBLE
            getData()

            binding.createEvent.setOnClickListener {
                val intent =   Intent(this@BarrenEventDetailActivity,CreateEventsActivity ::class.java)
                intent.putExtra("eventDesc",eventDesc)
                intent.putExtra("eventImage",eventImageUrl)
                intent.putExtra("location",location)
                intent.putExtra("eventTitle",eventTitle)
                intent.putExtra("MODE",1)
                startActivity(intent)
            }
        }else{
            getData()
        }

    }
    private fun getData() {

//getting data from intent
        eventDesc = intent.getStringExtra("eventDesc").toString()
        eventImageUrl = intent.getStringExtra("eventImage").toString()
        location = intent.getStringExtra("location").toString()
        eventTitle = intent.getStringExtra("eventTitle").toString()

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
        binding.eventTitleFr.text = eventTitle

    }
}