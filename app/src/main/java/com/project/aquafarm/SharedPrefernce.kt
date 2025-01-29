package com.project.aquafarm

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "userId"
        private const val KEY_PHONE_NUMBER = "phone"
        private const val LOGIN_STATUS = "loginStatus"
        private const val KEY_EMAILS = "emails" // Added for emails
        private const val KEY_PHONES = "phones" // Added for phones

    }

    fun saveUserId(userId: String) {
        sharedPreferences.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun savePhoneNumber(phone: String) {
        sharedPreferences.edit().putString(KEY_PHONE_NUMBER, phone).apply()
    }

    fun getUserId(): String {
        return sharedPreferences.getString(KEY_USER_ID, "") ?: ""
    }
    fun saveLoginStatus(loginStatus: String) {
        sharedPreferences.edit().putString(LOGIN_STATUS, loginStatus).apply()
    }

    // Save updated first and last name
    fun saveFirstName(firstName: String) {
        val editor = sharedPreferences.edit()
        editor.putString("first_name", firstName)
        editor.apply()
    }

    fun saveLastName(lastName: String) {
        val editor = sharedPreferences.edit()
        editor.putString("last_name", lastName)
        editor.apply()
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getString(LOGIN_STATUS, "false") == "true"
    }

    fun saveRegisteredEmail(email: String) {
        val emails = sharedPreferences.getStringSet(KEY_EMAILS, mutableSetOf()) ?: mutableSetOf()
        emails.add(email)
        sharedPreferences.edit().putStringSet(KEY_EMAILS, emails).apply()
    }

    fun saveRegisteredPhone(phone: String) {
        val phones = sharedPreferences.getStringSet(KEY_PHONES, mutableSetOf()) ?: mutableSetOf()
        phones.add(phone)
        sharedPreferences.edit().putStringSet(KEY_PHONES, phones).apply()
    }

    fun isEmailRegistered(email: String): Boolean {
        val emails = sharedPreferences.getStringSet(KEY_EMAILS, emptySet())
        return emails?.contains(email) == true
    }

    fun isPhoneRegistered(phone: String): Boolean {
        val phones = sharedPreferences.getStringSet(KEY_PHONES, emptySet())
        return phones?.contains(phone) == true
    }

}