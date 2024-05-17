package com.example.weatherapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.citiesData.Location
import com.example.weatherapp.citiesData.cities
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var city_input: EditText
    private lateinit var search_button: Button

    var selectedCity: String = ""

    var apiKey: String = "8e5f50d6100ca5b69804a5694b8614a8"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        city_input = findViewById<EditText>(R.id.city_name)
        search_button = findViewById<Button>(R.id.find_city)

        search_button.setOnClickListener {
            val cityName: String = city_input.text.toString()
            if (cityName.isNotEmpty())
                getGeoLocations(cityName,this)

        }


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

//            fetchWeather(selectedCity)
//            fetchForecast(selectedCity)

            builder.setTitle("NOWA LOKALIZACJA")
            builder.setMessage(selectedCity)
//            builder.setNeutralButton("Otwórz") { dialog, which ->
//                if(!isTablet()){
//                    val intent = Intent(this, QuickWeatherView::class.java)
//                    intent.putExtra("location", check.name)
//                    intent.putExtra("tempUnit", actualTempUnit.toString())
//                    intent.putExtra("distUnit", actualDistUnit.toString())
//                    startActivity(intent)
//                }
//                else {
//                    val intent = Intent(this, QuickWeatherTablet::class.java)
//                    intent.putExtra("location", check.name)
//                    intent.putExtra("tempUnit", actualTempUnit.toString())
//                    intent.putExtra("distUnit", actualDistUnit.toString())
//                    startActivity(intent)
//                }
//            }
//            builder.setPositiveButton("Dodaj do listy") { dialog, which ->
//                var locationList = allCities as MutableSet<String>
//                val added: Set<String> = (locationList + check.name).toSet()
//                saveLocations(added)
//                addButtonWithRemoveButton(layout, added.size - 1, check.name)
//                fetchWeather(selectedCity)
//                fetchForecast(selectedCity)
//                allCities = added as MutableSet<String>
//                city_input.text.clear()
//            }
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


}