package com.project.aquafarm.signup

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
import com.project.aquafarm.api.ApiUtilities
import com.project.aquafarm.databinding.ActivitySignupBinding
import com.project.aquafarm.login.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)

        binding.hidePassword.setOnClickListener(this)
        binding.hideConfirmPassword.setOnClickListener(this)

        setContentView(binding.root)

        val text = "Already Registered? Login"
        val spannableString = SpannableString(text)

// Set color for "Already Registered" (Gray)
        spannableString.setSpan(
            ForegroundColorSpan(0xFFFFFFFF.toInt()), // Gray color
            0,
            18,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

// Set color for "Login" and make it clickable
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#00008B") // Set the clickable text color to deep blue
                ds.isUnderlineText = false // Remove underline
            }
        }

// Apply clickable span to "Login"
        spannableString.setSpan(
            clickableSpan,
            19,
            text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

// Set the spannable string to the TextView
        binding.alredyRegistered.text = spannableString

// Set the entire TextView text color to white
        binding.alredyRegistered.setTextColor(Color.WHITE)

// Enable clickable behavior for the TextView
        binding.alredyRegistered.movementMethod = LinkMovementMethod.getInstance()

        //API call
        binding.signupButton.setOnClickListener {

            val email = binding.email.text.toString().trim()
            val phone = binding.phoneNumber.text.toString().trim()

            val (isValid, errorMessage) = checkAllFields()

            if (isValid) {
                val signupParams = HashMap<String?, String>()
                signupParams["first_name"] = binding.firstName.text.toString()
                signupParams["last_name"] = binding.lastName.text.toString()
                signupParams["phone"] = phone
                signupParams["email"] = email
                signupParams["password"] = binding.password.text.toString()

                lifecycleScope.launch(Dispatchers.IO) {
                    val response = ApiUtilities.getInstance().userSignup(signupParams)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            if (result?.status == true) {
                                startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                                Toast.makeText(applicationContext, "User Registration successful", Toast.LENGTH_SHORT).show()
                            } else {
                                when (result?.message) {
                                    "EMAIL_EXISTS" -> binding.email.error = "Email already exists. Try another."
                                    "PHONE_EXISTS" -> binding.phoneNumber.error = "Phone number already exists. Try another."
                                    else -> Toast.makeText(applicationContext, result?.message ?: "Something went wrong.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(applicationContext, "Error occurred. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, errorMessage ?: "Invalid input. Please check your entries.", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun checkAllFields(): Pair<Boolean, String?> {
        // Initialize validity and error message
        var isValid = true
        var errorMessage: String? = null

        // Reset errors
        binding.firstName.error = null
        binding.lastName.error = null
        binding.phoneNumber.error = null
        binding.email.error = null
        binding.password.error = null
        binding.confirmPassword.error = null

        // Pattern for alphabetic characters and spaces
        val namePattern = "^[A-Za-z]+( [A-Za-z]+)*$"

        // First Name
        val firstName = binding.firstName.text.toString().trim()
        if (firstName.isEmpty()) {
            binding.firstName.error = "Name is required"
            errorMessage = "Name is required"
            isValid = false
        } else if (firstName.length < 2 || firstName.length > 20) {
            binding.firstName.error = "First name must be between 2 and 20 characters"
            errorMessage = "First name must be between 2 and 20 characters"
            isValid = false
        } else if (!firstName.matches(namePattern.toRegex())) {
            binding.firstName.error = "Invalid name. Only alphabetic characters and spaces allowed"
            errorMessage = "Invalid name. Only alphabetic characters and spaces allowed"
            isValid = false
        }

        // Last Name
        val lastName = binding.lastName.text.toString().trim()
        if (isValid && lastName.isEmpty()) {
            binding.lastName.error = "Name is required"
            errorMessage = "Name is required"
            isValid = false
        } else if (isValid && (lastName.isEmpty() || lastName.length > 20)) {
            binding.lastName.error = "Last name must be between 2 and 20 characters"
            errorMessage = "Last name must be between 2 and 20 characters"
            isValid = false
        } else if (isValid && !lastName.matches(namePattern.toRegex())) {
            binding.lastName.error = "Invalid name. Only alphabetic characters and spaces allowed"
            errorMessage = "Invalid name. Only alphabetic characters and spaces allowed"
            isValid = false
        }

        // Phone
        val phoneNumber = binding.phoneNumber.text.toString().trim()
        val phonePattern = "^[+]?[0-9]{10,12}$"

        if (isValid && phoneNumber.isEmpty()) {
            binding.phoneNumber.error = "Phone number is required"
            errorMessage = "Phone number is required"
            isValid = false
        } else if (isValid && !phoneNumber.matches(phonePattern.toRegex())) {
            binding.phoneNumber.error = "Enter a valid phone number"
            errorMessage = "Enter a valid phone number"
            isValid = false
        } else if (isValid && phoneNumber.replace("+", "").all { it == '0' }) {
            binding.phoneNumber.error = "Phone number cannot contain only zeros"
            errorMessage = "Phone number cannot contain only zeros"
            isValid = false
        }

        // Email
        val email = binding.email.text.toString().trim()
        if (isValid && email.isEmpty()) {
            binding.email.error = "Email is required"
            errorMessage = "Email is required"
            isValid = false
        } else if (isValid && !isEmailValid(email)) {
            binding.email.error = "Invalid email address"
            errorMessage = "Invalid email address"
            isValid = false
        }


        // Password validation
        val password = binding.password.text.toString()
        val passwordPattern = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#\$%^&+=!]).{6,8}$"

        if (password.isEmpty()) {
            binding.password.error = "Password is required"
            errorMessage = "Password is required"
            isValid = false
        } else if (!password.matches(passwordPattern.toRegex())) {
            binding.password.error =
                "Password must be 6-8 characters long and include at least one uppercase letter, one number, and one special character"
            errorMessage =
                "Password must be 6-8 characters long and include at least one uppercase letter, one number, and one special character"
            isValid = false
        }

        // Confirm Password
        val confirmPassword = binding.confirmPassword.text.toString()
        if (isValid && confirmPassword.isEmpty()) {
            binding.confirmPassword.error = "Confirm Password is required"
            errorMessage = "Confirm Password is required"
            isValid = false
        } else if (isValid && password != confirmPassword) {
            binding.confirmPassword.error = "Passwords do not match"
            errorMessage = "Passwords do not match"
            isValid = false
        }

        return Pair(isValid, errorMessage ?: "Invalid input. Please check your entries.")
    }


    // Helper function to validate email
    private fun isEmailValid(email: String): Boolean {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false
        }
        val domainPart = email.substringAfterLast(".", "")
        return domainPart.length >= 2 // Ensure TLD is at least 2 characters long
    }
    private fun showHidePassWord(v: View?) {
        if (v?.id == R.id.hidePassword) {
            if (binding.password.transformationMethod
                    .equals(PasswordTransformationMethod.getInstance())
            ) {
                binding.hidePassword.setImageResource(R.drawable.ic_hide_eyes)
                binding.password.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.hidePassword.setImageResource(R.drawable.ic_password_eye)
                binding.password.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
        }
    }

    private fun hideConfirmPassword(v: View?) {
        if (v?.id == R.id.hideConfirmPassword) {
            if (binding.confirmPassword.transformationMethod
                    .equals(PasswordTransformationMethod.getInstance())
            ) {
                binding.hideConfirmPassword.setImageResource(R.drawable.ic_hide_eyes)
                binding.confirmPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.hideConfirmPassword.setImageResource(R.drawable.ic_password_eye)
                binding.confirmPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.hidePassword -> {
                showHidePassWord(v)
            }

            R.id.hideConfirmPassword -> {
                hideConfirmPassword(v)
            }
        }
    }

}
