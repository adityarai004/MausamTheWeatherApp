package com.example.mausam_theweatherapp

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import org.w3c.dom.Text

class MainActivity2 : AppCompatActivity() {
    private lateinit var requestQueue: RequestQueue
    private lateinit var recyclerView: RecyclerView
    private var defaultInput = "surat"
    private var hourTempList = mutableListOf<HoursItemViewModel>()
    private lateinit var currTempTV:TextView
    private lateinit var currConditionTV:TextView
    private lateinit var currLocationTV:TextView
    private lateinit var currFeelsLikeTV:TextView

    private lateinit var tmrDateTV:TextView
    private lateinit var tmrMaxTemp:TextView
    private lateinit var tmrMinTemp:TextView

    private lateinit var dayAfterTmrDateTV:TextView
    private lateinit var dayAfterTmrMaxTemp:TextView
    private lateinit var dayAfterTmrMinTemp:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currTempTV = findViewById(R.id.current_temp_tv)
        currConditionTV = findViewById(R.id.current_condition_tv)
        currLocationTV = findViewById(R.id.current_location_tv)
        currFeelsLikeTV = findViewById(R.id.current_feels_like_tv)
        recyclerView = findViewById(R.id.hourly_temp_rv)

        tmrDateTV = findViewById(R.id.tomorrow_weather_date_tv)
        tmrMaxTemp = findViewById(R.id.tomorrow_max_temp_tv)
        tmrMinTemp = findViewById(R.id.tomorrow_min_temp_tv)
        dayAfterTmrDateTV = findViewById(R.id.day_after_tomorrow_weather_date_tv)
        dayAfterTmrMaxTemp = findViewById(R.id.day_after_tomorrow_max_temp_tv)
        dayAfterTmrMinTemp = findViewById(R.id.day_after_tomorrow_min_temp_tv)
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
        val url = "https://api.weatherapi.com/v1/forecast.json?key=76580266a75e40b999f170314220605&q=${input}&days=3"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val current = response.getJSONObject("current")
                val location = response.getJSONObject("location")
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

                currTempTV.text = current.getString("temp_c") + "°"
                currLocationTV.text = location.getString("name")
                val currFeelsLikeString: String = "${(forecastDayIndexZero.getJSONObject("day").getString("maxtemp_c"))}° / ${(forecastDayIndexZero.getJSONObject("day").getString("mintemp_c"))}° Feels Like ${current.getString("feelslike_c")}°"
                currFeelsLikeTV.text = currFeelsLikeString
                currConditionTV.text = current.getJSONObject("condition").getString("text")

                //tomorrow
                val forecastDayIndexOne = forecastDay.getJSONObject(1)
                tmrDateTV.text = forecastDayIndexOne.getString("date")
                tmrMaxTemp.text = forecastDayIndexOne.getJSONObject("day").getString("maxtemp_c")
                tmrMinTemp.text = forecastDayIndexOne.getJSONObject("day").getString("mintemp_c")

                //day after tomorrow
                val forecastDayIndexTwo = forecastDay.getJSONObject(2)
                dayAfterTmrDateTV.text = forecastDayIndexTwo.getString("date")
                dayAfterTmrMaxTemp.text = forecastDayIndexTwo.getJSONObject("day").getString("maxtemp_c")
                dayAfterTmrMinTemp.text = forecastDayIndexTwo.getJSONObject("day").getString("mintemp_c")
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