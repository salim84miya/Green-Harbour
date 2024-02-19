package com.example.greenharbour.Utils

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

fun uploadImage(uri: Uri, folder:String, callback:(String)->Unit){

    var imageUrl :String

    FirebaseStorage.getInstance().getReference(folder).child(UUID.randomUUID().toString()).
            putFile(uri).addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {
                    imageUrl = it.toString()
                    callback(imageUrl)
                }
    }

}

