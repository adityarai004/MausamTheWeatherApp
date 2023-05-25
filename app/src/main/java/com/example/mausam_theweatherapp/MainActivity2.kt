package com.example.mausam_theweatherapp

import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.JsonObject
import org.json.JSONObject

class MainActivity2 : AppCompatActivity() {
    lateinit var requestQueue: RequestQueue
    private var defaultInput = "surat"
    lateinit  var hourTempList : List<HourlyTempViewModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val recyclerView: RecyclerView = findViewById(R.id.hourly_temp_rv)

        val cache = DiskBasedCache(cacheDir, 1024 * 1024) // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        val network = BasicNetwork(HurlStack())

        // Instantiate the RequestQueue with the cache and network. Start the queue.
        requestQueue = RequestQueue(cache, network).apply {
            start()
        }

    }
    private fun fetchData(input: String) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=76580266a75e40b999f170314220605&q=${input}&days=1"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val forecastObj = response.getJSONObject("forecast")
                val forecastDay = forecastObj.getJSONArray("forecastday")
                val forecastDayIndexZero = forecastDay.getJSONObject(0)
                val hour = forecastDayIndexZero.getJSONArray("hour")

                var everyHourForecast = mutableListOf<JSONObject>()
                for (i in 0..23){
                    everyHourForecast.add(hour.getJSONObject(i))
                }
                for(i in 0..23){
                    Log.e("TAG", everyHourForecast[i].getString("temp_c"))
                }
            },
            { error ->
            }
        )
        requestQueue.add(jsonObjectRequest)
    }
    override fun onStart() {
        super.onStart()
        fetchData(defaultInput)
    }
}