package com.example.weatherapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.weatherapp.R

class MainDataFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val city = arguments?.getString("city")
        val temp = arguments?.getString("temperature")
        val status = arguments?.getString("status")
        val time = arguments?.getString("time")
        val minTemp = arguments?.getString("minTemp")
        val maxTemp = arguments?.getString("maxTemp")
        val sunrise = arguments?.getString("sunrise")
        val sunset = arguments?.getString("sunset")
        val weatherID = arguments?.getString("weatherID")
        val feelLikeTemp = arguments?.getString("feelLike")

        Log.v("basic", city.toString())
        Log.v("basic", temp.toString())
        Log.v("basic", status.toString())
        Log.v("basic", time.toString())


        view.findViewById<TextView>(R.id.cityName).text = city
        view.findViewById<TextView>(R.id.temperature).text = temp
        view.findViewById<TextView>(R.id.overallStatus).text = status
        view.findViewById<TextView>(R.id.time).text = time
        view.findViewById<TextView>(R.id.tempMin).text = minTemp
        view.findViewById<TextView>(R.id.tempMax).text = maxTemp
        view.findViewById<TextView>(R.id.sunrise).text = sunrise
        view.findViewById<TextView>(R.id.sunset).text = sunset
        view.findViewById<TextView>(R.id.feelsLike).text = feelLikeTemp
        if (weatherID != null) {
            setWeatherIcon(view, weatherID)
        }
    }

    private fun setWeatherIcon(view: View, id: String) {
        when(id){
            "800" -> view.findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.sun)
            "801" -> view.findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.few_clouds)
            "802" -> view.findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.scattered_clouds)
            "803" -> view.findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.broken_clouds)
            "521" -> view.findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.shower_rain)
            "500" -> view.findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.rain)
            "211" -> view.findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.thunderstorm)
            "601" -> view.findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.snow)
            "701" -> view.findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.mist)
        }
    }
}