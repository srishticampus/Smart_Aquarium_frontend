package com.project.aquafarm.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.aquafarm.R
import com.project.aquafarm.SharedPreferencesManager
import com.project.aquafarm.dashboard.DashBoardActivity
import com.project.aquafarm.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    private val splashTimeout = 3000L
    private lateinit var sharedPreferences: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        sharedPreferences = SharedPreferencesManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (sharedPreferences.isUserLoggedIn()) {
                startActivity(Intent(this, DashBoardActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, splashTimeout)
    }
}
