package com.example.weatherapp.phone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.weatherapp.R
import com.example.weatherapp.utils.Distance
import com.example.weatherapp.utils.Temperatures

class WeatherViewPagerPhone : AppCompatActivity() {

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

    override fun onBackPressed () {
        super.onBackPressed()
        super.onPause()
    }
}
