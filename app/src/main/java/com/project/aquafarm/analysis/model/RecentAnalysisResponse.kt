package com.project.aquafarm.analysis.model

data class RecentAnalysisResponse(
    val data : List<Data>,
    val message: String,
    val status: Boolean
)