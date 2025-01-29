package com.project.aquafarm.suggestion

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.project.aquafarm.R
import com.project.aquafarm.suggestion.model.SuggestionItem

class SuggestionAdapter(private val items: List<SuggestionItem>) :
    RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_suggestion, parent, false)
        return SuggestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        if (position == 0) {
            holder.phRangeTextView.text = "pH Range"
            holder.fishTextView.text = "Fish Suggestions"

            holder.phRangeTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.blue))
            holder.fishTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.blue))
        } else {
            val item = items[position - 1]
            holder.phRangeTextView.text = item.phRange
            holder.fishTextView.text = item.fish

            val blackColor = ContextCompat.getColor(holder.itemView.context, R.color.black)
            holder.phRangeTextView.setTextColor(blackColor)
            holder.fishTextView.setTextColor(blackColor)

            // Reset bold styling
            holder.phRangeTextView.setTypeface(null, Typeface.NORMAL)
            holder.fishTextView.setTypeface(null, Typeface.NORMAL)
        }

    }

    override fun getItemCount(): Int {
        return items.size + 1 // +1 for the header
    }

    class SuggestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val phRangeTextView: TextView = view.findViewById(R.id.phRangeTextView)
        val fishTextView: TextView = view.findViewById(R.id.fishTextView)
    }
}
