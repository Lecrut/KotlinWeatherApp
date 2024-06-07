package com.example.weatherapp.models.forecastData

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)