package com.example.greenharbour.Utils

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.greenharbour.R

import java.net.URLEncoder

object Geocoder {

    fun getLatLngFromAddress(context: Context, address: String, callback: (latitude: Double, longitude: Double) -> Unit) {
        val queue = Volley.newRequestQueue(context)
        val apiKey = "AIzaSyCE-07OqyB1ze3XgMCta9qH95p4bhENiLg"
        // Replace with your actual API key
        val url = "https://maps.googleapis.com/maps/api/geocode/json?address=${URLEncoder.encode(address, "UTF-8")}&key=$apiKey"

        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            try {
                val results = response.getJSONArray("results")
                val firstResult = results.getJSONObject(0)
                val geometry = firstResult.getJSONObject("geometry")
                val location = geometry.getJSONObject("location")
                val latitude = location.getDouble("lat")
                val longitude = location.getDouble("lng")
                callback(latitude, longitude)
            } catch (e: Exception) {
                // Handle parsing errors
                e.printStackTrace()
                callback(0.0, 0.0)
            }
        }, { error ->
            // Handle network errors
            error.printStackTrace()
            callback(0.0, 0.0)
        })

        queue.add(request)
    }
}
