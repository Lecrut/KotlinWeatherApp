package com.example.weatherapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.citiesData.Location
import com.example.weatherapp.citiesData.cities
import com.example.weatherapp.forecastData.forecastData
import com.example.weatherapp.phone.FastViewPhone
import com.example.weatherapp.phone.WeatherViewPagerPhone
import com.example.weatherapp.tablet.FastViewTablet
import com.example.weatherapp.tablet.WeatherViewTablet
import com.example.weatherapp.utils.Distance
import com.example.weatherapp.utils.Temperatures
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import layout.weatherData
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.util.Timer
import java.util.TimerTask
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private lateinit var city_input: EditText
    private lateinit var search_button: Button
    private lateinit var layout: LinearLayout


    var selectedCity: String = ""
    var listOfCities: MutableSet<String>? = null
    var actualTempUnit: Temperatures = Temperatures.CELSIUS
    var actualDistUnit: Distance = Distance.METERS

    var apiKey: String = "8e5f50d6100ca5b69804a5694b8614a8"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        city_input = findViewById<EditText>(R.id.city_name)
        search_button = findViewById<Button>(R.id.find_city)
        layout = findViewById<LinearLayout>(R.id.cityList)

        val locationList: Set<String> = loadLocations()
        listOfCities = locationList.toMutableSet()

        search_button.setOnClickListener {
            val cityName: String = city_input.text.toString()
            if (cityName.isNotEmpty())
                getGeoLocations(cityName,this)

        }

        runTimer()


    }

    private fun getGeoLocations(city: String, context: Context) {
        val url = "http://api.openweathermap.org/geo/1.0/direct?q=$city&limit=5&appid=$apiKey"

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient.Builder().cache(null).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.v("API-Geolocations", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
                if (json != null) {
                    Log.v("API-Geolocations", json)
                }
                if (response.isSuccessful && json != null) {
                    Log.v("API-Geolocations", "Correct location")
                    val locationsData = Gson().fromJson(json, cities::class.java)
                    runOnUiThread{
                        openDialogWithCities(context, locationsData)
                    }
                } else {
                    Log.v("API-Geolocations", "Incorrect location")
                }
            }
        })
    }

    private fun removeWeatherData(location: String) {
        val sharedPreferences = getSharedPreferences(location, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("weather")
        editor.apply()
    }

    private fun removeForecastData(location: String) {
        val sharedPreferences = getSharedPreferences(location, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("forecast")
        editor.apply()
    }

    private fun saveWeatherData(weather: weatherData, city: String) {
        if (listOfCities?.contains(city) == true) {
            removeWeatherData(city)
        }
        val sharedPreferences = getSharedPreferences(city, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        val gson = Gson()
        val json = gson.toJson(weather)
        editor?.putString("weather", json)
        editor?.apply()
    }

    private fun saveForecastData(forecast: forecastData, city: String) {
        if (listOfCities?.contains(city) == true) {
            removeForecastData(city)
        }
        val sharedPreferences = getSharedPreferences(city, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        val gson = Gson()
        val json = gson.toJson(forecast)
        editor?.putString("forecast", json)
        editor?.apply()
    }

    private fun getWeather(city: String) {
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&lang=pl"

        val request = Request.Builder()
            .url(url)
            .build()

        val client = OkHttpClient.Builder()
            .cache(null)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.v("API-Geolocations", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
                if (json != null) {
                    Log.v("API-Geolocations", json)

                }
                if (response.isSuccessful && json != null) {
                    val weatherData = Gson().fromJson(json, weatherData::class.java)
                    saveWeatherData(weatherData, city)

                } else {
                    Log.v("API-Geolocations", "Incorrect location")
                }
            }
        })
    }


    private fun getForecast(city: String) {
        val url = "https://api.openweathermap.org/data/2.5/forecast?q=$city&appid=$apiKey&lang=pl"

        val request = Request.Builder()
            .url(url)
            .addHeader("Cache-Control", "no-cache")
            .build()

        val client = OkHttpClient.Builder()
            .cache(null)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.v("API-Geolocations", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
                if (json != null) {
                    Log.v("API-Geolocations", json)
                }
                if (response.isSuccessful && json != null) {
                    val forecastData: forecastData = Gson().fromJson(json, forecastData::class.java)
                    saveForecastData(forecastData, city)
                } else {
                    Log.v("API-Geolocations", "Incorrect location")
                }
            }
        })
    }

    private fun openDialogWithCities(context: Context, searchedCities: cities){
        val citiesList = mutableListOf<Location>()
        var counter = 0
        searchedCities.forEach { citySearchItem ->
            Log.v("API-Geolocations", citySearchItem.name + ' ' + citySearchItem.country)
            citiesList.add(counter, Location(citySearchItem.name, citySearchItem.country))
            counter++
        }

        val citiesArray = citiesList.map { "${it.name}, ${it.country}" }.toTypedArray()

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Znaleziono: ")
        builder.setItems(citiesArray) { dialog, which ->
            val check = citiesList[which]
            selectedCity = check.name + ',' + check.country

            getWeather(selectedCity)
            getForecast(selectedCity)

            builder.setTitle("NOWA LOKALIZACJA")
            builder.setMessage(selectedCity)
            builder.setNeutralButton("Otwórz") { dialog, which ->
                if(!isTablet()){
                    val intent = Intent(this@MainActivity, FastViewPhone::class.java)
                    intent.putExtra("location", check.name)
                    intent.putExtra("tempUnit", actualTempUnit.toString())
                    intent.putExtra("distUnit", actualDistUnit.toString())
                    startActivity(intent)
                }
                else {
                    val intent = Intent(this@MainActivity, FastViewTablet::class.java)
                    intent.putExtra("location", check.name)
                    intent.putExtra("tempUnit", actualTempUnit.toString())
                    intent.putExtra("distUnit", actualDistUnit.toString())
                    startActivity(intent)
                }
            }
            builder.setPositiveButton("Dodaj do listy") { dialog, which ->
                val locationList = listOfCities as MutableSet<String>
                val added: MutableSet<String> = (locationList + selectedCity).toMutableSet()
                saveLocations(added)
                addButtonWithRemoveButton(layout, added.size - 1, selectedCity)
                getWeather(selectedCity)
                getForecast(selectedCity)
                listOfCities = added
                city_input.text.clear()
            }
            builder.setNegativeButton("Zamknij") { dialog, which ->
                city_input.text.clear()
            }
            builder.show()
        }
        builder.setNegativeButton("Anuluj") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }
    fun saveLocations(locations: Set<String>) {
        val sharedPreferences = getSharedPreferences("city_list", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(locations)
        Log.v("flaga json", json)
        editor.putString("locations", json)
        editor.apply()
    }

    private fun isTablet(): Boolean {
        val metrics = resources.displayMetrics
        val dpWidth = metrics.widthPixels / metrics.density
        val dpHeight = metrics.heightPixels / metrics.density
        val smallestWidth = min(dpWidth, dpHeight)
        return smallestWidth >= 600
    }

    private fun addButtonWithRemoveButton(layout: LinearLayout, buttonId: Int, location: String) {
        val buttonLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        val cityButton = Button(this).apply {
            text = location
            id = buttonId
            layoutParams = LinearLayout.LayoutParams(
                0, // Szerokość
                LinearLayout.LayoutParams.WRAP_CONTENT, // Wysokość
                1f) // Waga
            setOnClickListener {
                if(isTablet()){
                    val intent = Intent(this@MainActivity, WeatherViewTablet::class.java)
                    intent.putExtra("location", location)
                    intent.putExtra("tempUnit", actualTempUnit.toString())
                    intent.putExtra("distUnit", actualDistUnit.toString())
                    startActivity(intent)
                }
                else {
                    val intent = Intent(this@MainActivity, WeatherViewPagerPhone::class.java)
                    intent.putExtra("location", location)
                    intent.putExtra("tempUnit", actualTempUnit.toString())
                    intent.putExtra("distUnit", actualDistUnit.toString())
                    startActivity(intent)
                }
            }
        }

        val removeButton = Button(this).apply {
            text = "Usuń"
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT, // Wysokość
                0.2f) // waga
            setTextColor(Color.RED)
            setOnClickListener {
                layout.removeView(buttonLayout)
                removeLocation(location)
                listOfCities?.remove(location)

            }
        }
        buttonLayout.addView(cityButton)
        buttonLayout.addView(removeButton)
        layout.addView(buttonLayout)

    }

    private fun removeLocation(locationToRemove: String) {
        val sharedPreferences = getSharedPreferences("city_list", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("locations", null)
        val type = object : TypeToken<Set<String>>() {}.type
        var locations: Set<String> = gson.fromJson(json, type)

        if (locations.contains(locationToRemove)) {
            locations = locations.filter { it != locationToRemove }.toSet()
            val editor = sharedPreferences.edit()
            val newJson = gson.toJson(locations)
            editor.remove(locationToRemove)
            editor.putString("locations", newJson)
            editor.apply()

        }

        val dir = File(filesDir.parent + "/shared_prefs/")
        val file = File(dir, "$locationToRemove.xml")

        if (file.exists()) {
            file.delete()
        }
    }

    private fun loadLocations(): Set<String> {
        val sharedPreferences = getSharedPreferences("city_list", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("locations", null)
        val type = object : TypeToken<Set<String>>() {}.type
        return gson.fromJson(json, type) ?: emptySet()
    }

    private fun runTimer() {
        val timer = Timer()
        timer.schedule(object : TimerTask(){
            override fun run() {
                listOfCities?.forEachIndexed{ id, location ->
                    getWeather(location)
                    getForecast(location)
                }
            }
        },0,  15000
        )
    }

}