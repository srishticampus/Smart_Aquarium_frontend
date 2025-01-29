package com.project.aquafarm.profile

import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.aquafarm.R
import com.project.aquafarm.SharedPreferencesManager
import com.project.aquafarm.api.ApiUtilities
import com.project.aquafarm.databinding.ActivityEditProfileBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileActivity : AppCompatActivity() {

    private lateinit var firstNameEt: EditText
    private lateinit var lastnameEt: EditText
    private lateinit var phoneEt: EditText
    private lateinit var emailEt: EditText
    private lateinit var profileImage: ImageView
    private lateinit var passwordEt: EditText
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    lateinit var binding: ActivityEditProfileBinding

    private var originalFirstName: String = ""
    private var originalLastName: String = ""
    private var originalEmail: String = ""
    private var originalPhone: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesManager = SharedPreferencesManager(applicationContext)

        firstNameEt = binding.firstNameEditText
        lastnameEt = binding.lastNameEditText
        emailEt = binding.emailEditText
        phoneEt = binding.phoneEditText


        val userId: String = sharedPreferencesManager.getUserId()

        viewProfile(userId)

        binding.confirm.setOnClickListener {
            updateProfile(userId)
        }

        binding.arrowLeft.setOnClickListener {
            finish()
        }

    }

    fun viewProfile(userId: String) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val api = ApiUtilities.getInstance()
                val result = api.viewProfile(userId)
                withContext(Dispatchers.Main) {
                    result.body()?.let { root ->
                        if (root.status == true) {
                            // Set fields
                            firstNameEt.setText(root.userData[0].first_name)
                            lastnameEt.setText(root.userData[0].last_name)
                            emailEt.setText(root.userData[0].email)
                            phoneEt.setText(root.userData[0].phone)

                            // Store original data
                            setOriginalProfileData(
                                root.userData[0].first_name,
                                root.userData[0].last_name,
                                root.userData[0].email,
                                root.userData[0].phone
                            )
                        } else {
                            Toast.makeText(
                                applicationContext,
                                root.message ?: "Error fetching data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } ?: Toast.makeText(
                        applicationContext,
                        "Server Error: ${result.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (_: Exception) {
            Toast.makeText(applicationContext, "An error occurred", Toast.LENGTH_SHORT).show()
        }
    }

    fun setOriginalProfileData(firstName: String, lastName: String, email: String, phone: String) {
        originalFirstName = firstName
        originalLastName = lastName
        originalEmail = email
        originalPhone = phone

        // Populate the input fields as well
        binding.firstNameEditText.setText(firstName)
        binding.lastNameEditText.setText(lastName)
        binding.emailEditText.setText(email)
        binding.phoneEditText.setText(phone)
    }

    fun updateProfile(userId: String) {
        // Get current values from EditText fields
        val currentFirstName = firstNameEt.text.toString()
        val currentLastName = lastnameEt.text.toString()
        val currentEmail = emailEt.text.toString()
        val currentPhone = phoneEt.text.toString()

        // Check if no changes are detected
        if (currentFirstName == originalFirstName &&
            currentLastName == originalLastName &&
            currentEmail == originalEmail &&
            currentPhone == originalPhone
        ) {
            // Show error toast and exit
            Toast.makeText(this, "No changes detected. Please make updates.", Toast.LENGTH_SHORT)
                .show()
            return
        }

        // Validate fields before proceeding
        if (!checkAllFields()) {
            return
        }

        // Proceed with API call if changes are detected
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiUtilities.getInstance().updateUserProfile(
                    id = userId.toInt(),
                    firstname = currentFirstName,
                    lastname = currentLastName,
                    email = currentEmail,
                    mobile = currentPhone
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.status == true) {
                            Toast.makeText(
                                applicationContext,
                                "Profile updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            sharedPreferencesManager.saveFirstName(currentFirstName)
                            sharedPreferencesManager.saveLastName(currentLastName)
                            // Update original values after successful update
                            setOriginalProfileData(
                                currentFirstName,
                                currentLastName,
                                currentEmail,
                                currentPhone
                            )
                        } else {
                            Toast.makeText(
                                applicationContext,
                                result?.message ?: "Update failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Server error: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        applicationContext,
                        "An error occurred: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun checkAllFields(): Boolean {

        var isValid = true

        // Reset errors
        binding.firstNameEditText.error = null
        binding.lastNameEditText.error = null
        binding.phoneEditText.error = null
        binding.emailEditText.error = null

        // Pattern to allow alphabetic characters and spaces
        val namePattern = "^[A-Za-z ]+$"

// First Name Validation
        val firstName = binding.firstNameEditText.text.toString().trim()
        if (firstName.isEmpty()) {
            binding.firstNameEditText.error = "Name is required"
            isValid = false
        } else if (firstName.length < 2 || firstName.length > 20) {
            binding.firstNameEditText.error = "First name must be between 2 and 20 characters"
            isValid = false
        } else if (!firstName.matches(namePattern.toRegex())) {
            binding.firstNameEditText.error =
                "Invalid name. Only alphabetic characters and spaces are allowed"
            isValid = false
        }

// Last Name Validation
        val lastName = binding.lastNameEditText.text.toString().trim()
        if (lastName.isEmpty()) {
            binding.lastNameEditText.error = "Name is required"
            isValid = false
        } else if (lastName.isEmpty() || lastName.length > 20) {
            binding.lastNameEditText.error = "Last name must be between 2 and 20 characters"
            isValid = false
        } else if (!lastName.matches(namePattern.toRegex())) {
            binding.lastNameEditText.error =
                "Invalid name. Only alphabetic characters and spaces are allowed"
            isValid = false
        }


        // Phone
        val phoneNumber = binding.phoneEditText.text.toString()
        val phonePattern = "^[+]?[0-9]{8,10}$"
        val isValidPhone = phoneNumber.matches(phonePattern.toRegex())
        if (phoneNumber.isEmpty()) {
            binding.phoneEditText.error = "Phone number is required"
            isValid = false
        } else if (!isValidPhone) {
            binding.phoneEditText.error = "Enter a valid phone number"
            isValid = false
        }

        // Email
        if (binding.emailEditText.length() == 0) {
            binding.emailEditText.error = "Email is required"
            isValid = false
        } else if (!isEmailValid(binding.emailEditText.text.toString())) {
            binding.emailEditText.error = "Invalid email address"
            isValid = false
        }
        return isValid
    }

    private fun isEmailValid(email: String): Boolean {
        val gmailRegex = "^[A-Za-z0-9._%+-]+@gmail\\.com$"
        return Regex(gmailRegex).matches(email)
    }

}