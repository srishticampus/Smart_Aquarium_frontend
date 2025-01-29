package com.project.aquafarm.suggestion

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.aquafarm.R
import com.project.aquafarm.dashboard.DashBoardActivity
import com.project.aquafarm.databinding.ActivitySuggesstionBinding
import com.project.aquafarm.suggestion.model.SuggestionItem

class SuggestionActivity : AppCompatActivity() {
    lateinit var binding: ActivitySuggesstionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySuggesstionBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_suggesstion)

        val recyclerView = findViewById<RecyclerView>(R.id.suggestionRecyclerView)

        // Sample data for suggestions
        val suggestionList = listOf(
            SuggestionItem("2.0 - 4.0", "Goldfish, Catfish"),
            SuggestionItem("4.0 - 6.5", "Tetras, Guppies"),
            SuggestionItem("6.5 - 8.5", "Tilapia, Bass"),
            SuggestionItem("8.5 - 10.0", "Cichlids, Mollies"),
            SuggestionItem("5.0 - 6.0", "Discus, Neon Tetras"),
            SuggestionItem("6.0 - 7.0", "Angelfish, Gouramis"),
            SuggestionItem("7.0 - 7.5", "Barbs, Swordtails"),
            SuggestionItem("7.5 - 8.0", "Rainbowfish, Zebra Danios"),
            SuggestionItem("8.0 - 8.5", "Lake Malawi Cichlids, Platies"),
            SuggestionItem("4.5 - 5.5", "Cardinal Tetras, Apistogramma"),
            SuggestionItem("5.5 - 6.5", "Pearl Gouramis, Kuhli Loaches"),
            SuggestionItem("7.5 - 9.0", "African Cichlids, Silver Dollars"),
            SuggestionItem("6.0 - 7.5", "Kribensis, Bolivian Rams"),
            SuggestionItem("8.0 - 9.0", "Mexican Mollies, Livebearers"),
            SuggestionItem("7.0 - 8.0", "Guppies, Endlers")
        )

        val adapter = SuggestionAdapter(suggestionList)
        recyclerView.layoutManager = GridLayoutManager(this, 1) // 1 row per item
        recyclerView.adapter = adapter

        binding.arrowLeft.setOnClickListener {

            val intent = Intent(this@SuggestionActivity, DashBoardActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}