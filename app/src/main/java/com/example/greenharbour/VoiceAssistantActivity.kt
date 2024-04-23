package com.example.greenharbour

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject

class VoiceAssistantActivity : AppCompatActivity() {

    private var TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_voice_assistant)

        val btn = findViewById<Button>(R.id.button)
        val input_number = findViewById<TextInputLayout>(R.id.input_number)

        btn.setOnClickListener {
            val number = input_number.editText!!.text.toString()
            getCall("+91"+number)
        }

    }

    private fun getCall( number:String){
        // Instantiate the RequestQueue
        val queue: RequestQueue = Volley.newRequestQueue(applicationContext)
        val url = "https://api.bland.ai/v1/calls"

// Create the JSON object to be sent in the request body
        val requestBody = JSONObject()
        requestBody.put("phone_number", number)
        requestBody.put("first_sentence","Welcome I can tell you the types of tree you can plant just tell me the location ")
        requestBody.put("task", "Keep the conversation formal")
        requestBody.put("max_duration",2)
// Add other parameters as needed-



// Create the POST request
        val request = object : JsonObjectRequest(
            Method.POST, url, requestBody,
            Response.Listener { response ->
                // Handle the response here
                val jsonResponse = response.toString()
                val jsonObject = JSONObject(jsonResponse)
                val callId = jsonObject.getString("call_id")

                Log.d(TAG, jsonResponse)
                // Use callId for further processing if needed
            },
            Response.ErrorListener { error ->
                // Handle errors here
            }) {
            // Override getHeaders() to set request headers
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "sk-wfa1b1race2rnzur8iqonedtbgx0e1ajeazzipac08m4i7dqtdpldadlsux39ecn69"
                headers["Content-Type"] = "application/json"
                return headers
            }

        }


// Add the request to the RequestQueue
        queue.add(request)
    }
}