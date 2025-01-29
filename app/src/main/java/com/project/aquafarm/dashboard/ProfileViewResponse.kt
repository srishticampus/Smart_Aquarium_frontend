package com.project.aquafarm.dashboard

data class ProfileViewResponse(
    val message: String,
    val status: Boolean,
    val userData: List<ProfileData>
)