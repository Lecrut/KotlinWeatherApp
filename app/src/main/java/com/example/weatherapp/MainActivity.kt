package com.example.weatherapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var autoText: AutoCompleteTextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        autoText = findViewById<AutoCompleteTextView>(R.id.city)

        val locationList = arrayOf(
            "Warszawa", "Radom", "Piaseczno", "Piastów", "Piła", "Porosiuki", "Pilica"
        )

        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, locationList)

        autoText.setAdapter(adapter)
    }

}