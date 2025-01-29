package com.project.aquafarm.login

data class LoginResponse(
    val message: String,
    val status: Boolean,
    val userData: List<LoginData>
)