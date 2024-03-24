package com.example.greenharbour

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage

class FirebaseCloudStorageTest : AppCompatActivity() {

    private lateinit var image: ImageView
    private lateinit var upload: Button
    private lateinit var browse: Button
    private lateinit var uri:Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_cloud_storage_test)

        val reference = FirebaseStorage.getInstance()

        image = findViewById(R.id.uploadedImage)
        upload = findViewById(R.id.uploadBtn)
        browse = findViewById(R.id.browseBtn)

        val galleryImage = registerForActivityResult(ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                image.setImageURI(it)
                if(it!==null){
                    uri = it
                }

            })

        browse.setOnClickListener {
            galleryImage.launch("image/*")
        }

        upload.setOnClickListener {
                reference.getReference("images").child(System.currentTimeMillis().toString())
                    .putFile(uri).addOnSuccessListener {
                        it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                            //add these url in your database
                        }
                    }
        }
    }
}