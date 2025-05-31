package com.venus.xiaohongshu.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.venus.xiaohongshu.data.model.User

/**
 * SessionManager class to handle user session data
 */
class SessionManager(context: Context) {
    private val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()
    private val gson = Gson()
    
    companion object {
        private const val PREF_NAME = "VenusXiaohongshuPrefs"
        private const val KEY_USER = "user_data"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val TAG = "SessionManager"
    }
    
    /**
     * Save user data to shared preferences
     */
    fun saveUserData(user: User?, token: String?) {
        try {
            if (user != null) {
                val userJson = gson.toJson(user)
                editor.putString(KEY_USER, userJson)
                Log.d(TAG, "User data saved: $userJson")
            }
            
            if (token != null) {
                editor.putString(KEY_TOKEN, token)
                Log.d(TAG, "Token saved")
            }
            
            editor.putBoolean(KEY_IS_LOGGED_IN, user != null && token != null)
            editor.apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving user data", e)
        }
    }
    
    /**
     * Get saved user object
     */
    fun getUser(): User? {
        val userJson = pref.getString(KEY_USER, null)
        return try {
            if (userJson != null) {
                gson.fromJson(userJson, User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving user data", e)
            null
        }
    }
    
    /**
     * Get authentication token
     */
    fun getToken(): String? {
        return pref.getString(KEY_TOKEN, null)
    }
    
    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    /**
     * Clear user data from preferences
     */
    fun clearUserData() {
        editor.remove(KEY_USER)
        editor.remove(KEY_TOKEN)
        editor.putBoolean(KEY_IS_LOGGED_IN, false)
        editor.apply()
        Log.d(TAG, "User session cleared")
    }
} 