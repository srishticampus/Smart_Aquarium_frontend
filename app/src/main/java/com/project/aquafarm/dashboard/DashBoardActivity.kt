package com.project.aquafarm.dashboard

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.CycleInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.google.firebase.database.*
import com.project.aquafarm.R
import com.project.aquafarm.analysis.AnalysisActivity
import com.project.aquafarm.databinding.ActivityDashBoardBinding
import com.project.aquafarm.profile.ProfileActivity
import com.project.aquafarm.suggestion.SuggestionActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DashBoardActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDashBoardBinding
    private lateinit var database: DatabaseReference

    private val previousValues = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navigate to Profile Activity
        binding.profileIcon.setOnClickListener {
            val intent = Intent(applicationContext, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Initialize Firebase database listener
        initializeDatabaseListener()

        val analysisBtn = findViewById<AppCompatButton>(R.id.analysisBtn)
        shakeButton(analysisBtn)

        // Navigate to AnalysisActivity
        analysisBtn.setOnClickListener {
            navigateToAnalysis()
        }

        binding.autoControlSwitch.setOnCheckedChangeListener { _, isChecked ->
            val state = if (isChecked) "on" else "off"
            updateSwitchState("automatic", state)

            binding.waterPumpSwitch.isEnabled = !isChecked
            if (isChecked) {
                binding.waterPumpSwitch.isChecked = false
                updateSwitchState("waterPump", "off")
            }
        }

        binding.waterPumpSwitch.setOnCheckedChangeListener { _, isChecked ->
            val state = if (isChecked) "on" else "off"
            updateSwitchState("waterPump", state)

            binding.autoControlSwitch.isEnabled = !isChecked
            if (isChecked) {
                binding.autoControlSwitch.isChecked = false
                updateSwitchState("automatic", "off")
            }
        }

        binding.suggestionBtn.setOnClickListener(this)
    }

    private fun initializeDatabaseListener() {
        database = FirebaseDatabase.getInstance().reference

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val ph_value = snapshot.child("Sensor/phLevel").value.toString()
                    val water_level = snapshot.child("Sensor/distance").value.toString()
                    val ambience_light = snapshot.child("Sensor/light").value.toString()
                    val temperature = snapshot.child("Sensor/temperature").value.toString()
                    val oxygen = snapshot.child("Sensor/humidity").value.toString()

                    val currentDate =
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

                    binding.phValue.text = ph_value
                    binding.tempValue.text = temperature

                    updateProgressBarWithValue(
                        "waterLevel",
                        water_level,
                        binding.progressWater,
                        binding.waterValue
                    )
                    updateProgressBarWithValue(
                        "light",
                        ambience_light,
                        binding.progressLight,
                        binding.lightValue
                    )
                    updateProgressBarWithValue(
                        "oxygen",
                        oxygen,
                        binding.progressOxygen,
                        binding.o2value
                    )

                    if (shouldSendData(
                            ph_value,
                            temperature,
                            ambience_light,
                            oxygen,
                            water_level
                        )
                    ) {
                        sendDataToPhpServer(
                            ph_value,
                            temperature,
                            ambience_light,
                            oxygen,
                            water_level,
                            currentDate,
                            currentTime
                        )
                    }
                } catch (e: Exception) {
                    Log.e("FirebaseError", "Error fetching data: ${e.message}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    applicationContext,
                    "Failed to read sensor data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun shouldSendData(vararg values: String): Boolean {
        return values.any { it != "N/A" }
    }

    private fun sendDataToPhpServer(
        phLevel: String,
        temperature: String,
        ambientLight: String,
        oxygen: String,
        waterLevel: String,
        currentDate: String,
        currentTime: String
    ) {
        val json = JSONObject().apply {
            put("ph_value", phLevel)
            put("temperature", temperature)
            put("ambience_light", ambientLight)
            put("oxygen", oxygen)
            put("water_level", waterLevel)
            put("date", currentDate)
            put("time", currentTime)
        }

        val jsonString = json.toString()
        val requestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("http://campus.sicsglobal.co.in/Project/Aqua_farm/api/store_sensor_data.php")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("PHP Server", "Failed to send data: ${e.message}")
                runOnUiThread {
                    Toast.makeText(
                        this@DashBoardActivity,
                        "Failed to send data to server.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful) {
                    Log.d("PHP Server", "Data sent successfully: $responseBody")
                } else {
                    Log.e("PHP Server", "Server Error: ${response.code}")
                }
            }
        })
    }

    private fun updateProgressBarWithValue(
        key: String,
        value: String,
        progressBar: ProgressBar,
        textView: TextView
    ) {
        if (previousValues[key] != value) {
            previousValues[key] = value

            val numericValue = value.toIntOrNull() ?: 0
            progressBar.progress = numericValue
            textView.text = value

            val textColor: Int = when {
                numericValue < 50 -> ContextCompat.getColor(this, R.color.color_low)
                numericValue in 50..80 -> ContextCompat.getColor(this, R.color.color_normal)
                else -> ContextCompat.getColor(this, R.color.color_high)
            }

            textView.setTextColor(textColor)
        }
    }

    private fun updateSwitchState(switch: String, state: String) {
        database.child("settings").child(switch).setValue(state)
            .addOnSuccessListener {
                Log.d("Firebase", "$switch switch state updated to $state")
            }
            .addOnFailureListener {
                Log.e("FirebaseError", "Failed to update $switch state: ${it.message}")
            }
    }

    private fun shakeButton(button: AppCompatButton) {
        val shake = ObjectAnimator.ofFloat(
            button,
            "translationX",
            0f,
            25f,
            -25f,
            20f,
            -20f,
            10f,
            -10f,
            5f,
            -5f,
            0f
        )
        shake.duration = 5000
        shake.interpolator = CycleInterpolator(1f)
        shake.start()
    }

    private fun navigateToAnalysis() {
        val intent = Intent(this, AnalysisActivity::class.java)
        startActivity(intent)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.suggestionBtn -> {
                suggestionClick()
            }
        }
    }

    private fun suggestionClick() {

        val intent = Intent(this@DashBoardActivity, SuggestionActivity::class.java)
        startActivity(intent)
    }

}
