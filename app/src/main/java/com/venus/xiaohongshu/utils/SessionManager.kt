package com.venus.xiaohongshu.utils

import android.content.Context
import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.venus.xiaohongshu.data.model.User

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    
    companion object {
        const val PREF_NAME = "shenglingji_prefs"
        const val USER_TOKEN = "user_token"
        const val USER_DATA = "user_data"
        const val IS_LOGGED_IN = "is_logged_in"
    }
    
    // 保存用户登录信息
    fun saveAuthUser(token: String, user: User) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        
        val userAdapter = moshi.adapter(User::class.java)
        val userJson = userAdapter.toJson(user)
        editor.putString(USER_DATA, userJson)
        
        editor.putBoolean(IS_LOGGED_IN, true)
        editor.apply()
    }
    
    // 获取用户Token
    fun getToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }
    
    // 获取已登录用户信息
    fun getUser(): User? {
        val userJson = prefs.getString(USER_DATA, null) ?: return null
        val userAdapter = moshi.adapter(User::class.java)
        return userAdapter.fromJson(userJson)
    }
    
    // 更新用户信息
    fun updateUser(user: User) {
        val editor = prefs.edit()
        val userAdapter = moshi.adapter(User::class.java)
        val userJson = userAdapter.toJson(user)
        editor.putString(USER_DATA, userJson)
        editor.apply()
    }
    
    // 检查用户是否已登录
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGGED_IN, false)
    }
    
    // 注销用户
    fun logout() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
} 