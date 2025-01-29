//package com.project.aquafarm.login
//
//import android.content.Intent
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.view.View
//import android.view.animation.AlphaAnimation
//import android.widget.LinearLayout
//import android.widget.TextView
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import com.project.aquafarm.R
//import com.project.aquafarm.dashboard.DashBoardActivity
//import com.project.aquafarm.databinding.ActivityLoginSplashBinding
//
//class LoginSplashActivity : AppCompatActivity() {
//    private val splashTimeout = 3000L
//    lateinit var linear: LinearLayout
//    lateinit var binding: ActivityLoginSplashBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        binding = ActivityLoginSplashBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val animatedTextView: TextView = findViewById(R.id.animatedTextView)
//        // Start the animation
//        fadeInTextView(animatedTextView)
//
////        linear = binding.lyt
////        var animTextview = WaveyTextView(this@LoginSplashActivity)
////        animTextview.setPadding(0,75,0,75)
////        animTextview.textSize = 28.0F
////        animTextview.setText("Welcome To AquaFarm")
////        animTextview.animateToRight(30000.00)
////        linear.addView(animTextview)
////
//        Handler(Looper.getMainLooper()).postDelayed(
//            {
//                val intent = Intent(this, DashBoardActivity::class.java)
//                startActivity(intent)
//                overridePendingTransition(0, 0)
//                finish()
//            }, splashTimeout
//        )
//    }
//
//    private fun fadeInTextView(textView: TextView) {
//        // Set the TextView to be visible before starting the animation
//        textView.visibility = View.VISIBLE
//
//        // Create an AlphaAnimation
//        val fadeIn = AlphaAnimation(0f, 1f).apply {
//            duration = 2000 // Duration in milliseconds
//            fillAfter = true // Keep the view visible after animation ends
//        }
//
//        // Start the animation
//        textView.startAnimation(fadeIn)
//    }
//}