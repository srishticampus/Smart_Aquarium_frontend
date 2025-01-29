package com.project.aquafarm.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.project.aquafarm.SharedPreferencesManager
import com.project.aquafarm.api.ApiUtilities
import com.project.aquafarm.dashboard.DashBoardActivity
import com.project.aquafarm.databinding.ActivityProfileBinding
import com.project.aquafarm.login.LoginActivity
import com.project.aquafarm.resetpassword.ResetPassword
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileBinding
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    private lateinit var firstNameEt: TextView
    private lateinit var phoneEt: TextView
    private lateinit var emailEt: TextView

    //  private lateinit var profileImage: ImageView
    var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        sharedPreferencesManager = SharedPreferencesManager(applicationContext)

        setContentView(binding.root)

        firstNameEt = binding.nameTV
        emailEt = binding.emailaddress
        phoneEt = binding.phoneNumber

        val userId: String = sharedPreferencesManager.getUserId()
        viewProfile(userId)

//        binding.confirmButton.setOnClickListener {
//            updateProfile(userId)
//        }

        binding.logoutBtn.setOnClickListener {
            // Show a confirmation dialog
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout?")
            builder.setPositiveButton("Yes") { dialog, _ ->
                // Perform logout action
                sharedPreferencesManager.saveLoginStatus("loggedOut")

                // Clear activity stack and navigate to LoginActivity
                val intent = Intent(applicationContext, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                dialog.dismiss()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                // Dismiss the dialog
                dialog.dismiss()
            }
            // Show the dialog
            builder.create().show()
        }


        binding.resetLyt.setOnClickListener {
            startActivity(Intent(applicationContext, ResetPassword::class.java))
        }

        binding.editLyt.setOnClickListener {
            startActivity(Intent(applicationContext, EditProfileActivity::class.java))
        }

        binding.arrowLeft.setOnClickListener {

            val intent = Intent(this@ProfileActivity, DashBoardActivity::class.java)
            startActivity(intent)
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
                            val fullName =
                                "${root.userData[0].first_name} ${root.userData[0].last_name}"
                            firstNameEt.text = fullName

                            emailEt.setText(root.userData[0].email)
                            phoneEt.setText(root.userData[0].phone)

                            // profileImage.setImageResource(root.userData[0].photo)
                            // passwordEt.setText(root.userData[0].p)
                        }
                    } ?: Toast.makeText(
                        this@ProfileActivity,
                        "Server Error: ${result.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (_: Exception) {

        }
    }


//    fun updateProfile(userId: String) {
//
//        CoroutineScope(Dispatchers.IO).launch {
//            val filesDir = applicationContext.filesDir
//            val file = File(filesDir, "image.jpg")
//            val inputStream =
//                imageUri?.let { applicationContext.contentResolver.openInputStream(it) }
//            val outputStream = FileOutputStream(file)
//            inputStream?.copyTo(outputStream)
//            val requestFile: RequestBody =
//                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//
//            val multipartBody: MultipartBody.Part =
//                MultipartBody.Part.createFormData("image", file.name, requestFile)
//
//            val userId = RequestBody.create(MultipartBody.FORM, userId)
//            val firstname = RequestBody.create(MultipartBody.FORM, firstNameEt.text.toString())
//            val lastname = RequestBody.create(MultipartBody.FORM, lastnameEt.text.toString())
//            val email = RequestBody.create(MultipartBody.FORM, emailEt.text.toString())
//            val phone = RequestBody.create(MultipartBody.FORM, phoneEt.text.toString())
//
//            val map = HashMap<String?, RequestBody?>()
//            map["userid"] = userId
//            map["first_name"] = firstname
//            map["last_name"] = lastname
//            map["phone"] = phone
//            map["email"] = email
//
//            val response = ApiUtilities.getInstance().updateUserProfile(map, multipartBody)
//            withContext(Dispatchers.Main) {
//                if (response.isSuccessful) {
//                    val result = response.body()
//                    if (result != null) {
//                        if (result.status == true) {
//                            Toast.makeText(this@ProfileActivity, "Success", Toast.LENGTH_SHORT)
//                                .show()
//                        } else {
//                            Toast.makeText(this@ProfileActivity, result.message, Toast.LENGTH_SHORT)
//                                .show()
//                        }
//                    }
//                } else {
//                    Toast.makeText(this@ProfileActivity, "Failed", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
}