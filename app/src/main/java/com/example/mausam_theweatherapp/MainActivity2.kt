package com.example.mausam_theweatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class MainActivity2 : AppCompatActivity() {
    lateinit var requestQueue: RequestQueue
    lateinit var recyclerView: RecyclerView
    private var defaultInput = "surat"
    var hourTempList = mutableListOf<HoursItemViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.hourly_temp_rv)
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true)
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

                val everyHourForecast = mutableListOf<JSONObject>()
                for (i in 23 downTo 0){
                    everyHourForecast.add(hour.getJSONObject(i))
                    hourTempList.add(
                        HoursItemViewModel(
                            hour.getJSONObject(i).getString("time").subSequence(11,16).toString(),
                            hour.getJSONObject(i).getDouble("temp_c"),
                            hour.getJSONObject(i).getJSONObject("condition").getString("icon")
                        )
                    )
                }
                if(hourTempList.isNotEmpty()){
                    val adapter = HourlyAdapter(this,hourTempList)
                    recyclerView.adapter = adapter
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