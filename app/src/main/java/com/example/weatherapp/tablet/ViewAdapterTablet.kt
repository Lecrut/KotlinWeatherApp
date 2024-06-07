package com.example.weatherapp.tablet

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.weatherapp.models.forecastData.forecastData
import com.example.weatherapp.fragments.AdditionalDataFragment
import com.example.weatherapp.fragments.ForecastDataFragment
import com.example.weatherapp.fragments.MainDataFragment
import com.example.weatherapp.models.utils.Distance
import com.example.weatherapp.models.utils.Temperatures
import com.google.gson.Gson
import layout.weatherData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.util.Timer
import java.util.TimerTask

class ViewAdapterTablet(private val activity: FragmentActivity, loc: String, temp: Temperatures, dist: Distance) : FragmentStateAdapter(activity) {

    private val frag_items = 3
    private var weather: weatherData? = null
    private var forecast: forecastData? = null
    private var location = loc
    private var tempUnit = temp
    private var distUnit = dist
    init {
        weather = loadWeatherData()
        forecast = loadForecastData()
        setTimer()
    }
    override fun getItemCount(): Int {
        return frag_items
    }
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> setBasicFrag()
            1 -> setAdditionalFrag()
            else -> setForecastFrag()
        }
    }

    private fun loadWeatherData(): weatherData? {
        val sharedPreferences = activity.getSharedPreferences(location, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences?.getString("weather", "")
        return gson.fromJson(json, weatherData::class.java)
    }

    private fun loadForecastData(): forecastData? {
        val sharedPreferences = activity.getSharedPreferences(location, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences?.getString("forecast", "")
        return gson.fromJson(json, forecastData::class.java)
    }


    private fun setBasicFrag () : MainDataFragment {
        val basicDataFrag = MainDataFragment()
        val bundle = Bundle()

        bundle.putString("city", weather?.name)
        bundle.putString("temperature", weather?.main?.let { temperatureConvert(it.temp, tempUnit) })
        bundle.putString("status", weather?.weather?.get(0)?.description)
        bundle.putString("weatherID", weather?.weather?.get(0)?.id.toString())
        bundle.putString("time",
            weather?.dt?.let { timeStampConvert(it.toLong(), weather!!.timezone) })
        bundle.putString("minTemp", weather?.main?.let { temperatureConvert(it.temp_min, tempUnit) })
        bundle.putString("maxTemp", weather?.main?.let { temperatureConvert(it.temp_max, tempUnit) })
        bundle.putString("sunrise",
            weather?.sys?.sunrise?.let { shortTimeStamp(it.toLong(), weather!!.timezone) })
        bundle.putString("sunset",
            weather?.sys?.sunset?.let { shortTimeStamp(it.toLong(), weather!!.timezone) })
        bundle.putString("feelLike",
            weather?.main?.feels_like?.let { temperatureConvert(it, tempUnit) })

        basicDataFrag.arguments = bundle
//        addFragment(R.id.fragment_basic_weather, basicDataFrag)
        return basicDataFrag
    }

    private fun setAdditionalFrag() : AdditionalDataFragment {
        val additionalDataFrag = AdditionalDataFragment()
        val bundle = Bundle()

        bundle.putString("pressure", weather?.main?.pressure.toString() + " hPa")
        bundle.putString("humidity", weather?.main?.humidity.toString() +  " %")
        bundle.putString("wind", weather?.wind?.speed.toString().format("%.1f") + distanceConvert(distUnit) + "/s")
        bundle.putString("direction", weather?.wind?.deg.toString())
        bundle.putString("cloudiness", weather?.clouds?.all.toString() + " %")
        bundle.putString("visibility", visibilityConvert(weather?.visibility.toString(), distUnit))

        additionalDataFrag.arguments = bundle
        return additionalDataFrag
    }

    private fun setForecastFrag() : ForecastDataFragment {
        val forecastFrag = ForecastDataFragment()
        val bundle = Bundle()

        bundle.putString("tempFirstDay",
            forecast?.list?.get(4)?.main?.let { temperatureConvert(it.temp, tempUnit) })
        bundle.putString("firstDayDate",
            forecast?.list?.get(4)?.dt?.let { dateTimeStamp(it.toLong(), forecast!!.city.timezone) })
        bundle.putString("firstWeatherID", forecast?.list?.get(4)?.weather?.get(0)?.id.toString())

        bundle.putString("tempSecondDay",
            forecast?.list?.get(12)?.main?.let { temperatureConvert(it.temp, tempUnit) })
        bundle.putString("secondDayDate",
            forecast?.city?.let { dateTimeStamp(forecast!!.list[12].dt.toLong(), it.timezone) })
        bundle.putString("secondWeatherID", forecast?.list?.get(12)?.weather?.get(0)?.id.toString())

        bundle.putString("tempThirdDay",
            forecast?.list?.get(20)?.main?.let { temperatureConvert(it.temp, tempUnit) })
        bundle.putString("thirdDayDate",
            forecast?.city?.let { dateTimeStamp(forecast!!.list[20].dt.toLong(), it.timezone) })
        bundle.putString("thirdWeatherID", forecast?.list?.get(20)?.weather?.get(0)?.id.toString())

        bundle.putString("tempFourthDay",
            forecast?.list?.get(28)?.main?.let { temperatureConvert(it.temp, tempUnit) })
        bundle.putString("fourthDayDate",
            forecast?.city?.let { dateTimeStamp(forecast!!.list[28].dt.toLong(), it.timezone) })
        bundle.putString("FourthWeatherID", forecast?.list?.get(28)?.weather?.get(0)?.id.toString())

        bundle.putString("tempFifthDay",
            forecast?.list?.get(36)?.main?.let { temperatureConvert(it.temp, tempUnit) })
        bundle.putString("fifthDayDate",
            forecast?.city?.let { dateTimeStamp(forecast!!.list[36].dt.toLong(), it.timezone) })
        bundle.putString("fifthWeatherID", forecast?.list?.get(36)?.weather?.get(0)?.id.toString())

        forecastFrag.arguments = bundle
//        addFragment(R.id.fragment_forecast, forecastFrag)
        return forecastFrag
    }
    private fun timeStampConvert(time: Long, timezone: Int) : String {
        val date = Date(time * 1000)

        val hours = timezone / 3600
        val minutes = (timezone % 3600) / 60
        val timeZone = String.format("GMT%+d:%02d", hours, minutes)

        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
        dateFormat.timeZone = TimeZone.getTimeZone(timeZone)

        return dateFormat.format(date)
    }

    private fun shortTimeStamp(time: Long, timezone: Int) : String {
        val date = Date(time * 1000)

        val hours = timezone / 3600
        val minutes = (timezone % 3600) / 60
        val timeZone = String.format("GMT%+d:%02d", hours, minutes)

        val dateFormat = SimpleDateFormat("HH:mm")
        dateFormat.timeZone = TimeZone.getTimeZone(timeZone)

        return dateFormat.format(date)
    }

    private fun dateTimeStamp(time: Long, timezone: Int) : String {
        val date = Date(time * 1000)

        val hours = timezone / 3600
        val minutes = (timezone % 3600) / 60
        val timeZone = String.format("GMT%+d:%02d", hours, minutes)

        val dateFormat = SimpleDateFormat("EEEE")
        dateFormat.timeZone = TimeZone.getTimeZone(timeZone)

        var result = " "
        when(dateFormat.format(date)){
            "Monday" -> result = "Poniedziałek"
            "Tuesday" -> result = "Wtorek"
            "Wednesday" -> result = "Środa"
            "Thursday" -> result = "Czwartek"
            "Friday" -> result = "Piątek"
            "Saturday" -> result = "Sobota"
            "Sunday" -> result = "Niedziela"
        }
        return result
    }

    private fun temperatureConvert(kelvin: Double, tempEnum: Temperatures) : String {
        var temp = kelvin.toInt()
        when(tempEnum){
            Temperatures.CELSIUS -> {
                temp -= 273
                return "$temp°C"
            }
            Temperatures.KELVINS -> return "$temp°K"
            Temperatures.FAHRENHEITS -> {
                temp = ((temp * 9)/5 - 459.67).toInt()
                return "$temp°F"
            }
        }
    }

    private fun distanceConvert(dist: Distance) : String {
        when(dist){
            Distance.METERS -> return "m"
            Distance.MILES -> return "mi"
        }
    }

    private fun visibilityConvert(distance: String, unit: Distance) : String {
        when(unit){
            Distance.METERS -> return (distance.toInt() / 1000).toString().format("%.1f") + " km"
            Distance.MILES -> return (distance.toInt() * 0.0006214).toString().format("%.1f") + " mil"
        }
    }

    private fun setTimer() {
        val timer = Timer()
        timer.schedule(object : TimerTask(){
            override fun run() {
                loadForecastData()
                loadWeatherData()
            }
        },0,  1000
        )
    }
}