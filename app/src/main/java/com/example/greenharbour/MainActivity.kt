package com.example.greenharbour

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.greenharbour.Authorization.LoginActivity
import com.example.greenharbour.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signOutBtn.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this@MainActivity,LoginActivity::class.java))
            finish()
        }


    }
}