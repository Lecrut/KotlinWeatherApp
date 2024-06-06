package com.example.weatherapp.phone

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.weatherapp.R
import com.example.weatherapp.utils.Distance
import com.example.weatherapp.utils.Temperatures
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class FastViewPhone : AppCompatActivity() {

    private var location : String = ""
    private lateinit var viewPager: ViewPager2
    private var tempUnit: Temperatures = Temperatures.CELSIUS
    private var distUnit: Distance = Distance.METERS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        location = intent.getStringExtra("location").toString()

        var check = intent.getStringExtra("tempUnit").toString()
        tempUnit = Temperatures.valueOf(check)
        check = intent.getStringExtra("distUnit").toString()
        distUnit = Distance.valueOf(check)

        viewPager = findViewById(R.id.pager)
        viewPager.adapter = ViewPagerAdapterPhone(this, location, tempUnit, distUnit)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        removeLocation(location)
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
}