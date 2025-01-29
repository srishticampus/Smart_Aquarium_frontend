package com.project.aquafarm.analysis

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.project.aquafarm.R
import com.project.aquafarm.analysis.model.DataXX
import com.project.aquafarm.api.ApiUtilities
import com.project.aquafarm.databinding.ActivityAnalysisBinding
import kotlinx.coroutines.launch

class AnalysisActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnalysisBinding
    private val dateList = mutableListOf<String>()
    private lateinit var lineChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lineChart = findViewById(R.id.lineChart)

        binding.back.setOnClickListener { finish() }

        // Fetch available dates
        fetchAvailableDates()

        // Handle spinner selection
        binding.spinnerDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    val selectedDate = dateList[position]
                    fetchAnalysisDataByDate(selectedDate)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun fetchAvailableDates() {
        lifecycleScope.launch {
            try {
                val response = ApiUtilities.getInstance().selectDate()
                if (response.isSuccessful && response.body()?.status == true) {
                    dateList.clear()
                    dateList.add("Select a Date")
                    dateList.addAll(response.body()?.data?.map { it.date } ?: emptyList())

                    val adapter = ArrayAdapter(
                        this@AnalysisActivity,
                        android.R.layout.simple_spinner_item,
                        dateList
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerDate.adapter = adapter
                } else {
                    Toast.makeText(
                        this@AnalysisActivity,
                        response.body()?.message ?: "Failed to fetch dates",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@AnalysisActivity, "Error: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun fetchAnalysisDataByDate(date: String) {
        lifecycleScope.launch {
            try {
                val response = ApiUtilities.getInstance().getAnalysisDataByDate(date)
                if (response.isSuccessful && response.body()?.status == true) {
                    val data = response.body()?.data ?: emptyList()
                    updateChart(data)
                } else {
                    Toast.makeText(
                        this@AnalysisActivity,
                        response.body()?.message ?: "Failed to fetch data",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@AnalysisActivity, "Error: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun updateChart(data: List<DataXX>) {
        val entriesMap = mutableMapOf<String, MutableList<Entry>>()
        val timeLabels = data.map { it.time }

        val colors = mapOf(
            "pH Value" to 0xFFFF0000.toInt(),
            "Light Value" to 0xFF00FF00.toInt(),
            "Water Level" to 0xFF0000FF.toInt(),
            "Temperature" to 0xFFFFFF00.toInt(),
            "Oxygen" to 0xFFFF00FF.toInt()
        )

        data.forEachIndexed { index, record ->
            val xValue = index.toFloat()
            entriesMap.getOrPut("pH Value") { mutableListOf() }.add(Entry(xValue, record.ph_value.toFloat()))
            entriesMap.getOrPut("Light Value") { mutableListOf() }.add(Entry(xValue, record.ambience_light.toFloat()))
            entriesMap.getOrPut("Water Level") { mutableListOf() }.add(Entry(xValue, record.water_level.toFloat()))
            entriesMap.getOrPut("Temperature") { mutableListOf() }.add(Entry(xValue, record.temperature.toFloat()))
            entriesMap.getOrPut("Oxygen") { mutableListOf() }.add(Entry(xValue, record.oxygen.toFloat()))
        }

        val dataSets = entriesMap.map { (label, entries) ->
            LineDataSet(entries, label).apply {
                color = colors[label] ?: 0xFF000000.toInt()
                setCircleColor(color)
                circleRadius = 4f
                lineWidth = 2f
                valueTextSize = 10f
                setDrawFilled(true)
            }
        }

        val lineData = LineData(dataSets)
        lineChart.data = lineData
        lineChart.description.isEnabled = false

        lineChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f   // Ensure step is 1 unit between entries
            labelCount = 5
            setDrawGridLines(false)
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return timeLabels.getOrNull(value.toInt()) ?: ""
                }
            }
        }

        lineChart.axisLeft.granularity = 1f
        lineChart.axisRight.isEnabled = false
        lineChart.legend.isEnabled = true

        // Enable scrolling and zooming
        lineChart.isDragEnabled = true
        lineChart.setScaleEnabled(true)
        lineChart.setPinchZoom(true)
        lineChart.setVisibleXRangeMaximum(10f)
        lineChart.setVisibleXRangeMinimum(5f)
        lineChart.moveViewToX(0f)

        lineChart.invalidate()
    }
}

//No Data Available for Current Date'