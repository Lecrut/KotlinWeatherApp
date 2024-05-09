package com.example.weatherapp.forecastData

data class forecast (
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<forecastMid>,
    val message: Int
)