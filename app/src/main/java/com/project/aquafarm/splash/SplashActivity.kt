package com.project.aquafarm.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.aquafarm.R
import com.project.aquafarm.SharedPreferencesManager
import com.project.aquafarm.dashboard.DashBoardActivity
import com.project.aquafarm.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    private val splashTimeout = 2500L
    private lateinit var sharedPreferences: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        sharedPreferences = SharedPreferencesManager(this)

        val logoImage = findViewById<ImageView>(R.id.logoImage)
        val appName = findViewById<TextView>(R.id.appName)
        val tagline = findViewById<TextView>(R.id.tagline)
        val fishGif = findViewById<ImageView>(R.id.fishGif)

        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        val slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in)
        val fishSwimAnimation = AnimationUtils.loadAnimation(this, R.anim.fish_swim)


        logoImage.startAnimation(fadeInAnimation)
        appName.startAnimation(slideInAnimation)
        tagline.startAnimation(slideInAnimation)
        fishGif.startAnimation(fishSwimAnimation)


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
