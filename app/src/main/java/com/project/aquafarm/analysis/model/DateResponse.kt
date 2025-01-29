package com.project.aquafarm.analysis.model

data class DateResponse(
    val data: List<DataX>,
    val message: String,
    val status: Boolean
)