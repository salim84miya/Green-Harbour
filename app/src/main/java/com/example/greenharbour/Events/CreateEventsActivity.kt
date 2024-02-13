package com.example.greenharbour.Events

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.greenharbour.R
import com.example.greenharbour.databinding.ActivityCreateEventsBinding

class CreateEventsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateEventsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.eventsImg

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED
            ) {
                val permission = arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission,112)
            }
        }
    }
}