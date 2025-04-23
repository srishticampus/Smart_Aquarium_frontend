package com.project.aquafarm.suggestion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.aquafarm.R
import com.project.aquafarm.suggestion.model.Suggestion

class SuggestionAdapter(private val suggestionList: List<Suggestion>) :
    RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder>() {

    class SuggestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fishImage: ImageView = itemView.findViewById(R.id.fishImage)
        val fishName: TextView = itemView.findViewById(R.id.fishName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_suggestion, parent, false)
        return SuggestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        val suggestion = suggestionList[position]

        // Load image using Glide (Make sure you add Glide dependency)
        Glide.with(holder.itemView.context)
            .load(suggestion.image)
            .placeholder(R.drawable.guppy) // Add a placeholder image in res/drawable
            .into(holder.fishImage)

        holder.fishName.text = suggestion.name
    }

    override fun getItemCount(): Int = suggestionList.size
}
