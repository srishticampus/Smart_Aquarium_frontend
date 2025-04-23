package com.project.aquafarm.suggestion.model

data class SuggestionItem(
    val conditions: Conditions,
    val message: String,
    val status: Boolean,
    val suggestions: List<Suggestion>
)