package com.example.greenharbour

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.greenharbour.Authorization.LoginActivity
import com.example.greenharbour.Events.CreateEventsActivity
import com.example.greenharbour.Events.MyEventActivity
import com.example.greenharbour.Events.MyCreatedEventsActivity
import com.example.greenharbour.Events.NearbyEventActivity
import com.example.greenharbour.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signOutBtn.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }

        binding.createEventBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, CreateEventsActivity::class.java))
        }

        binding.eventsViewBtn.setOnClickListener {
<<<<<<< HEAD
            startActivity(Intent(this@MainActivity, NearbyEventsActivity::class.java))
=======
            startActivity(Intent(this@MainActivity,NearbyEventActivity::class.java))
        }

        binding.myEventBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity,MyCreatedEventsActivity::class.java))
>>>>>>> 4b69850f9bd6a11f1e56df67e5f6651f0002ac7d
        }


    }
}