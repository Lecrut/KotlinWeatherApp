package com.example.weatherapp.models.citiesData

data class citiesItem(
    val country: String,
    val lat: Double,
    val local_names: LocalNames,
    val lon: Double,
    val name: String,
    val state: String
)