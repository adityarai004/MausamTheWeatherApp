package com.example.mausam_theweatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest

class MainActivity2 : AppCompatActivity() {
    lateinit var tempTV:TextView
    private lateinit var tempET:EditText
    lateinit var getTempBtn:Button
    private lateinit var requestQueue: RequestQueue
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tempTV = findViewById(R.id.temp_tv)
        tempET = findViewById(R.id.city_name_et)
        getTempBtn = findViewById(R.id.get_temp_btn)



        val cache = DiskBasedCache(cacheDir, 1024 * 1024) // 1MB cap

// Set up the network to use HttpURLConnection as the HTTP client.
        val network = BasicNetwork(HurlStack())

// Instantiate the RequestQueue with the cache and network. Start the queue.
        requestQueue = RequestQueue(cache, network).apply {
            start()
        }


        getTempBtn.setOnClickListener {
            var input = tempET.text.toString()
            fetchData(input)
        }
    }

    private fun fetchData(input: String) {
        val url = "https://api.weatherapi.com/v1/current.json?key=76580266a75e40b999f170314220605&q=${input}"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val jsonObj = response.getJSONObject("current")
                val currTemp = jsonObj.getString("temp_c")
                tempTV.text = currTemp.toString()
            },
            { error ->
                Toast.makeText(applicationContext,"${error.toString()}",Toast.LENGTH_LONG).show()
            }
        )
        requestQueue.add(jsonObjectRequest)
    }
}