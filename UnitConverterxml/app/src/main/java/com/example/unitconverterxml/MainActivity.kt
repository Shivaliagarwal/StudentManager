package com.example.unitconverterxml

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var inputValue: EditText
    private lateinit var outputValue: TextView
    private lateinit var inputUnitSpinner: Spinner
    private lateinit var outputUnitSpinner: Spinner
    private lateinit var convertButton: Button

    private val conversionFactors = mapOf(
        "Centimeters" to 0.01,
        "Meters" to 1.00,
        "Millimeters" to 0.001,
        "Feet" to 0.3048
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputValue = findViewById(R.id.input_value)
        outputValue = findViewById(R.id.output_value)
        inputUnitSpinner = findViewById(R.id.input_unit_spinner)
        outputUnitSpinner = findViewById(R.id.output_unit_spinner)
        convertButton = findViewById(R.id.convert_button)

        val units = conversionFactors.keys.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, units)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        inputUnitSpinner.adapter = adapter
        outputUnitSpinner.adapter = adapter

        convertButton.setOnClickListener {
            convertUnits()
        }

        inputValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                convertUnits()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        inputUnitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                convertUnits()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        outputUnitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                convertUnits()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun convertUnits() {
        val inputValueStr = inputValue.text.toString()
        val inputValueDouble = inputValueStr.toDoubleOrNull() ?: 0.0
        val inputUnit = inputUnitSpinner.selectedItem.toString()
        val outputUnit = outputUnitSpinner.selectedItem.toString()
        val conversionFactor = conversionFactors[inputUnit] ?: 1.0
        val oConversionFactor = conversionFactors[outputUnit] ?: 1.0

        val result = (inputValueDouble * conversionFactor * 100.0 / oConversionFactor).toInt() / 100.0
        outputValue.text = "Result: $result $outputUnit"
    }
}
