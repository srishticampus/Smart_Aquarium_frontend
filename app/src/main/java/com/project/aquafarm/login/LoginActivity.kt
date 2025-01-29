package com.project.aquafarm.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.project.aquafarm.R
import com.project.aquafarm.SharedPreferencesManager
import com.project.aquafarm.api.ApiUtilities
import com.project.aquafarm.dashboard.DashBoardActivity
import com.project.aquafarm.databinding.ActivityLoginBinding
import com.project.aquafarm.signup.SignupActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreference: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        sharedPreference = SharedPreferencesManager(applicationContext)
        setContentView(binding.root)

        binding.hidePassword.setOnClickListener(this)

        val text = "New to AquaCare? Sign Up"
        val spannableString = SpannableString(text)
        spannableString.setSpan(
            ForegroundColorSpan(0xFFFFFFFF.toInt()),
            0, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                startActivity(Intent(applicationContext, SignupActivity::class.java))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#00008B")
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(clickableSpan, 16, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.newtoRainSensingTv.text = spannableString
        binding.newtoRainSensingTv.movementMethod = LinkMovementMethod.getInstance()

        binding.loginSubmit.setOnClickListener {
            val params = HashMap<String?, String>()
            params["phone"] = binding.userName.text.toString()
            params["password"] = binding.loginPassword.text.toString()

            if (checkAllFields()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val response = ApiUtilities.getInstance().userLogin(params)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            if (result?.status == true) {
                                result.userData.get(0).let {
                                    sharedPreference.saveUserId(it.userid)
                                    sharedPreference.savePhoneNumber(it.phone)
                                    sharedPreference.saveLoginStatus("true") // Save login status
                                }
                                val intent =
                                    Intent(this@LoginActivity, DashBoardActivity::class.java)
                                startActivity(intent)
                                finish() // Close login activity
                                Toast.makeText(
                                    applicationContext,
                                    "User Logged in successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    result?.message ?: "Login failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Enter the details", Toast.LENGTH_SHORT).show()
            }
        }


        binding.forgotTv.setOnClickListener {
            startActivity(Intent(applicationContext, ForgotPasswordActivity::class.java))
        }

    }

    private fun checkAllFields(): Boolean {
        var isValid = true
        if (binding.userName.length() == 0) {
            binding.userName.error = "Number is required"
            isValid = false
        }
        if (binding.loginPassword.length() == 0) {
            binding.loginPassword.error = "Password is required"
            isValid = false
        } else if (binding.loginPassword.length() < 4) {
            binding.loginPassword.error = "Password must be at least 4 characters"
            isValid = false
        }
        return isValid
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.hidePassword) {
            if (binding.loginPassword.transformationMethod == PasswordTransformationMethod.getInstance()) {
                binding.hidePassword.setImageResource(R.drawable.ic_password_eye)
                binding.loginPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                binding.hidePassword.setImageResource(R.drawable.ic_hide_eyes)
                binding.loginPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }
}
