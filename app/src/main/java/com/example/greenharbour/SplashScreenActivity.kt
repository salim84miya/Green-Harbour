package com.example.greenharbour


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.greenharbour.Authorization.SignUpActivity
import com.example.greenharbour.databinding.ActivitySplashScreenBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding :ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        Glide.with(this).load(R.raw.tree_growing).into(binding.splashScreenIcon)
        Handler(Looper.getMainLooper()).postDelayed({
            if(Firebase.auth.currentUser==null)
            startActivity(Intent(this@SplashScreenActivity, SignUpActivity::class.java))
            else
                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
            finish()
        }, 3200)
    }
}