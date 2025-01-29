package com.project.aquafarm.resetpassword

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.project.aquafarm.R
import com.project.aquafarm.SharedPreferencesManager
import com.project.aquafarm.api.ApiUtilities
import com.project.aquafarm.databinding.ActivityResetPasswordBinding
import com.project.aquafarm.login.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResetPassword : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityResetPasswordBinding
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        sharedPreferencesManager = SharedPreferencesManager(applicationContext)

        val userId: String = sharedPreferencesManager.getUserId()
        changePassword(userId)

        binding.hidePassword.setOnClickListener(this)
        binding.hideNewPassword.setOnClickListener(this)

    }

    fun changePassword(userId: String) {
        binding.submitReset.setOnClickListener {

            val params = HashMap<String?, String>()
            params["userid"] = userId
            params["current_password"] = binding.currentPassword.text.toString()
            params["new_password"] = binding.newPassword.text.toString()

            if (checkAllFields()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val response = ApiUtilities.getInstance().resetPassword(params)

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            if (result?.status == true) {
                                intent = Intent(this@ResetPassword, LoginActivity::class.java)
                                startActivity(intent)
                                Toast.makeText(
                                    applicationContext,
                                    "Password Reset Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    result?.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else {
                            Toast.makeText(
                                applicationContext,
                                response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Enter the fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkAllFields(): Boolean {
        var isValid = true

        // Reset errors
        binding.currentPassword.error = null
        binding.newPassword.error = null

        val currentPassword = binding.currentPassword.text.toString().trim()
        val newPassword = binding.newPassword.text.toString().trim()

        // Current Password Validation
        if (currentPassword.isEmpty()) {
            binding.currentPassword.error = "Current Password is required"
            isValid = false
        }

        // New Password Validation
        val passwordPattern = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#\$%^&+=!]).{6,8}$"

        if (newPassword.isEmpty()) {
            binding.newPassword.error = "Password is required"
            isValid = false
        } else if (newPassword.length < 6 || newPassword.length > 8) {
            binding.newPassword.error = "Password must be between 6 and 8 characters"
            isValid = false
        } else if (!newPassword.matches(passwordPattern.toRegex())) {
            binding.newPassword.error =
                "Password must contain at least one uppercase letter, one special character, and one number"
            isValid = false
        } else if (newPassword == currentPassword) {
            binding.newPassword.error = "New password cannot be the same as the current password"
            isValid = false
        }

        return isValid
    }

    private fun showHidePassWord(v: View?) {
        if (v?.id == R.id.hidePassword) {
            if (binding.currentPassword.transformationMethod
                    .equals(PasswordTransformationMethod.getInstance())
            ) {
                binding.hidePassword.setImageResource(R.drawable.ic_hide_eyes)
                binding.currentPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.hidePassword.setImageResource(R.drawable.ic_password_eye)
                binding.currentPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
        }
    }

    private fun hideConfirmPassword(v: View?) {
        if (v?.id == R.id.hideNewPassword) {
            if (binding.newPassword.transformationMethod
                    .equals(PasswordTransformationMethod.getInstance())
            ) {
                binding.hideNewPassword.setImageResource(R.drawable.ic_hide_eyes)
                binding.newPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.hideNewPassword.setImageResource(R.drawable.ic_password_eye)
                binding.newPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.hidePassword -> {
                showHidePassWord(v)
            }

            R.id.hideNewPassword -> {
                hideConfirmPassword(v)
            }
        }
    }

}