package com.project.aquafarm.suggestion

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.project.aquafarm.api.ApiUtilities
import com.project.aquafarm.dashboard.DashBoardActivity
import com.project.aquafarm.databinding.ActivitySuggesstionBinding
import com.project.aquafarm.suggestion.model.Suggestion
import kotlinx.coroutines.launch

class SuggestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuggesstionBinding
    private lateinit var suggestionAdapter: SuggestionAdapter
    private val suggestionList = mutableListOf<Suggestion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySuggesstionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchSensorData()

        binding.arrowLeft.setOnClickListener {
            startActivity(Intent(this, DashBoardActivity::class.java))
            finish()
        }
    }

    /**
     * Set up RecyclerView with empty list initially
     */
    private fun setupRecyclerView() {
        suggestionAdapter = SuggestionAdapter(suggestionList)
        binding.suggestionRecyclerView.apply {
            layoutManager = GridLayoutManager(this@SuggestionActivity, 2)
            adapter = suggestionAdapter
        }
    }

    /**
     * Fetch latest sensor data from Firebase
     */
    private fun fetchSensorData() {
        val database = FirebaseDatabase.getInstance()
        val sensorRef = database.getReference("sensors")

        sensorRef.get().addOnSuccessListener { snapshot ->
            val oxygen = snapshot.child("oxygen").getValue(String::class.java) ?: "0"
            val ph = snapshot.child("ph").getValue(String::class.java) ?: "0"
            val temperature = snapshot.child("temperature").getValue(String::class.java) ?: "0"

            getSuggestionsFromAPI(oxygen, ph, temperature)

        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch sensor data", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Call API to get fish/plant suggestions based on sensor data
     */
    private fun getSuggestionsFromAPI(oxygen: String, ph: String, temperature: String) {
        lifecycleScope.launch {
            try {
                val response = ApiUtilities.getInstance().getSuggestions(oxygen, ph, temperature)
                if (response.isSuccessful && response.body() != null) {
                    val newSuggestions = response.body()?.suggestions ?: emptyList()
                    updateRecyclerView(newSuggestions)
                } else {
                    Toast.makeText(
                        this@SuggestionActivity,
                        "Failed to get suggestions",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SuggestionActivity, "Error: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    /**
     * Update RecyclerView when new data arrives
     */
    private fun updateRecyclerView(newSuggestions: List<Suggestion>) {
        suggestionList.clear()
        suggestionList.addAll(newSuggestions)
        suggestionAdapter.notifyDataSetChanged()
    }
}
