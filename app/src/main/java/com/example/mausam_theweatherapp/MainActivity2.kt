package com.example.mausam_theweatherapp

import android.os.Bundle
import android.view.MotionEvent
import android.widget.GridView
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

class MainActivity2 : AppCompatActivity() {
    private lateinit var requestQueue: RequestQueue
    private lateinit var recyclerView: RecyclerView
    private lateinit var airQualityRV: RecyclerView
    private var defaultInput = "surat"
    private var hourTempList = mutableListOf<HoursItemViewModel>()
    private var astroList = ArrayList<AstroModel>()
    private var astroList2 = ArrayList<AstroModel>()
    private var pollutantList = ArrayList<PollutantViewModel>()
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

    private lateinit var astroGV:GridView
    private lateinit var astroGV2:GridView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currTempTV = findViewById(R.id.current_temp_tv)
        currConditionTV = findViewById(R.id.current_condition_tv)
        currLocationTV = findViewById(R.id.current_location_tv)
        currFeelsLikeTV = findViewById(R.id.current_feels_like_tv)
        recyclerView = findViewById(R.id.hourly_temp_rv)
        airQualityRV = findViewById(R.id.air_quality_rv)
        astroGV = findViewById(R.id.astro_gv)
        astroGV2 = findViewById(R.id.astro_gv_2)


        tmrDateTV = findViewById(R.id.tomorrow_weather_date_tv)
        tmrMaxTemp = findViewById(R.id.tomorrow_max_temp_tv)
        tmrMinTemp = findViewById(R.id.tomorrow_min_temp_tv)
        dayAfterTmrDateTV = findViewById(R.id.day_after_tomorrow_weather_date_tv)
        dayAfterTmrMaxTemp = findViewById(R.id.day_after_tomorrow_max_temp_tv)
        dayAfterTmrMinTemp = findViewById(R.id.day_after_tomorrow_min_temp_tv)
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true)

        airQualityRV.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        airQualityRV.isNestedScrollingEnabled = false //to remove jerky scrolling

        val cache = DiskBasedCache(cacheDir, 2048 * 2048) // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        val network = BasicNetwork(HurlStack())

        // Instantiate the RequestQueue with the cache and network. Start the queue.
        requestQueue = RequestQueue(cache, network).apply {
            start()
        }

        fetchData(defaultInput)
    }
    private fun fetchData(input: String) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=76580266a75e40b999f170314220605&q=${input}&days=3&aqi=yes"
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

                currTempTV.text = "${(current.getString("temp_c")).toDouble().toInt()}°"
                currLocationTV.text = location.getString("name")
                val currFeelsLikeString: String = "${(forecastDayIndexZero.getJSONObject("day").getString("maxtemp_c")).toDouble().toInt()}° / ${(forecastDayIndexZero.getJSONObject("day").getString("mintemp_c")).toDouble().toInt()}° Feels Like ${(current.getString("feelslike_c")).toDouble().toInt()}°"
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

                astroList.add(AstroModel(forecastDayIndexZero.getJSONObject("astro").getString("sunrise"),R.drawable.sunrise,"Sunrise"))
                astroList.add(AstroModel(forecastDayIndexZero.getJSONObject("astro").getString("sunset"),R.drawable.sunset,"Sunset"))
                astroList2.add(AstroModel(forecastDayIndexZero.getJSONObject("astro").getString("moonset"),R.drawable.moonrise,"Moonrise"))
                astroList2.add(AstroModel(forecastDayIndexZero.getJSONObject("astro").getString("moonrise"),R.drawable.moonset,"Moonset"))

                val astroAdapter = AstroGVAdapter(this,astroList)
                val astroAdapter2 = AstroGVAdapter(this,astroList2)
                astroGV.adapter = astroAdapter
                astroGV2.adapter = astroAdapter2


                val airQualityPollutants = current.getJSONObject("air_quality")
                pollutantList.add(PollutantViewModel("CO(Carbon Monoxide)",airQualityPollutants.getDouble("co")))
                pollutantList.add(PollutantViewModel("NO2(Nitrogen Dioxide)",airQualityPollutants.getDouble("no2")))
                pollutantList.add(PollutantViewModel("O3(Ozone)",airQualityPollutants.getDouble("o3")))
                pollutantList.add(PollutantViewModel("SO2(Sulphur Dioxide)",airQualityPollutants.getDouble("so2")))
                pollutantList.add(PollutantViewModel("PM2.5(Particulate Matter)",airQualityPollutants.getDouble("pm2_5")))
                pollutantList.add(PollutantViewModel("PM10(Particulate Matter)",airQualityPollutants.getDouble("pm10")))
                pollutantList.add(PollutantViewModel("Quality Index",airQualityPollutants.getDouble("us-epa-index")))



                val airQualityAdapter = AirQualityAdapter(this,pollutantList)
                airQualityRV.adapter = airQualityAdapter
            },
            { error ->
            }
        )
        requestQueue.add(jsonObjectRequest)
    }
}