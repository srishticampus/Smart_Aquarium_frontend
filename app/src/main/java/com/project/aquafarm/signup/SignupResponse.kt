package com.project.aquafarm.signup

data class SignupResponse(
    val message: String,
    val status: Boolean,
    val userData: List<SignupData>
)