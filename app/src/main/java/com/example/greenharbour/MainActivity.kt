package com.example.greenharbour

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.greenharbour.Authorization.LoginActivity
import com.example.greenharbour.Events.BarrenLocationActivity
import com.example.greenharbour.Events.BarrenLocationViewActivity
import com.example.greenharbour.Events.CreateEventsActivity
import com.example.greenharbour.Events.MyCreatedEventsActivity
import com.example.greenharbour.Events.NearbyBarrenLocation
import com.example.greenharbour.Events.NearbyEventActivity
import com.example.greenharbour.Models.NetworkManager
import com.example.greenharbour.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding

    private lateinit var dialog :AlertDialog
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarHome)


        val drawerLayout:DrawerLayout = findViewById(R.id.drawerLayout)
        val navigationView :NavigationView= findViewById(R.id.navView)

        val user_email = navigationView.getHeaderView(0).findViewById<TextView>(R.id.email)
        val user_username = navigationView.getHeaderView(0).findViewById<TextView>(R.id.username)
        val user_profile = navigationView.getHeaderView(0).findViewById<ImageView>(R.id.profile_image)

        user_profile.setOnClickListener {
            startActivity(Intent(this@MainActivity,UserAccountActivity::class.java))
        }
        Firebase.firestore.collection("Users").document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                if (it!=null && it.exists()){
                    user_email.text = it.get("login").toString()
                    user_username.text = it.get("username").toString()

                }

            }

        toggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.first-> {
                    startActivity(Intent(this@MainActivity,VoiceAssistantActivity::class.java))
                }
                R.id.second->{
                    Firebase.auth.signOut()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
                R.id.third->{
                    startActivity(Intent(this@MainActivity,BarrenLocationActivity::class.java))
                }
                R.id.sixth->{
                    startActivity(Intent(this@MainActivity,BarrenLocationViewActivity::class.java))
                }

            }
            true
        }

        binding.barrenLocationBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity,NearbyBarrenLocation::class.java))
        }

        dialog = MaterialAlertDialogBuilder(this,R.style.MaterialAlertDialog_Rounded)
            .setView(R.layout.custom_dialog)
            .setCancelable(false)
            .create()

        val networkManager = NetworkManager(this)

        networkManager.observe(this){
            if(!it){
                if(!dialog.isShowing)
                    dialog.show()
            }else{
                if(dialog.isShowing)
                    dialog.hide()
            }

        }


        binding.createEventBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, CreateEventsActivity::class.java))
        }

        binding.eventsViewBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity,NearbyEventActivity::class.java))
        }

        binding.myEventBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity,MyCreatedEventsActivity::class.java))
        }


    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }


}