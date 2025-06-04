// 定义包名，标识会话管理工具类的命名空间
package com.venus.xiaohongshu.utils

// 导入Android上下文类，用于访问应用程序环境
import android.content.Context
// 导入SharedPreferences类，用于本地数据存储
import android.content.SharedPreferences
// 导入Log类，用于日志记录
import android.util.Log
// 导入Gson库，用于JSON序列化和反序列化
import com.google.gson.Gson
// 导入用户数据模型类
import com.venus.xiaohongshu.data.model.User

/**
 * 会话管理器类，负责处理用户会话数据的存储和管理
 * 包括用户信息、认证令牌的保存、获取和清除功能
 * 使用SharedPreferences进行本地持久化存储
 * 
 * @param context Android应用上下文，用于访问SharedPreferences
 */
class SessionManager(context: Context) {
    // 创建SharedPreferences实例，使用私有模式确保数据安全
    private val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    // 获取SharedPreferences编辑器，用于修改存储的数据
    private val editor: SharedPreferences.Editor = pref.edit()
    // 创建Gson实例，用于用户对象的JSON序列化和反序列化
    private val gson = Gson()
    
    // 伴生对象，定义常量和静态成员
    companion object {
        // SharedPreferences文件名，用于标识存储文件
        private const val PREF_NAME = "VenusXiaohongshuPrefs"
        // 用户数据存储键名
        private const val KEY_USER = "user_data"
        // 认证令牌存储键名
        private const val KEY_TOKEN = "auth_token"
        // 登录状态存储键名
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        // 日志标签，用于标识SessionManager的日志输出
        private const val TAG = "SessionManager"
    }
    
    /**
     * 保存用户数据到SharedPreferences
     * 将用户对象序列化为JSON字符串存储，同时保存认证令牌
     * 
     * @param user 用户对象，可为null
     * @param token 认证令牌字符串，可为null
     */
    fun saveUserData(user: User?, token: String?) {
        try {
            // 如果用户对象不为空，则序列化并保存
            if (user != null) {
                // 将用户对象转换为JSON字符串
                val userJson = gson.toJson(user)
                // 将JSON字符串存储到SharedPreferences中
                editor.putString(KEY_USER, userJson)
                // 记录用户数据保存成功的日志
                Log.d(TAG, "User data saved: $userJson")
            }
            
            // 如果令牌不为空，则保存令牌
            if (token != null) {
                // 将认证令牌存储到SharedPreferences中
                editor.putString(KEY_TOKEN, token)
                // 记录令牌保存成功的日志
                Log.d(TAG, "Token saved")
            }
            
            // 设置登录状态：只有当用户和令牌都不为空时才认为已登录
            editor.putBoolean(KEY_IS_LOGGED_IN, user != null && token != null)
            // 提交所有更改到SharedPreferences
            editor.apply()
        } catch (e: Exception) {
            // 捕获异常并记录错误日志
            Log.e(TAG, "Error saving user data", e)
        }
    }
    
    /**
     * 获取保存的用户对象
     * 从SharedPreferences中读取JSON字符串并反序列化为User对象
     * 
     * @return 用户对象，如果不存在或解析失败则返回null
     */
    fun getUser(): User? {
        // 从SharedPreferences中获取用户JSON字符串，默认值为null
        val userJson = pref.getString(KEY_USER, null)
        return try {
            // 如果JSON字符串不为空，则反序列化为User对象
            if (userJson != null) {
                // 使用Gson将JSON字符串转换为User对象
                gson.fromJson(userJson, User::class.java)
            } else {
                // 如果JSON字符串为空，返回null
                null
            }
        } catch (e: Exception) {
            // 捕获反序列化异常并记录错误日志
            Log.e(TAG, "Error retrieving user data", e)
            // 发生异常时返回null
            null
        }
    }
    
    /**
     * 获取认证令牌
     * 从SharedPreferences中读取保存的认证令牌
     * 
     * @return 认证令牌字符串，如果不存在则返回null
     */
    fun getToken(): String? {
        // 从SharedPreferences中获取认证令牌，默认值为null
        return pref.getString(KEY_TOKEN, null)
    }
    
    /**
     * 检查用户是否已登录
     * 根据SharedPreferences中保存的登录状态标志判断
     * 
     * @return true表示用户已登录，false表示未登录
     */
    fun isLoggedIn(): Boolean {
        // 从SharedPreferences中获取登录状态，默认值为false
        return pref.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    /**
     * 清除用户数据
     * 从SharedPreferences中移除所有用户相关数据，实现用户登出功能
     */
    fun clearUserData() {
        // 移除用户数据
        editor.remove(KEY_USER)
        // 移除认证令牌
        editor.remove(KEY_TOKEN)
        // 设置登录状态为false
        editor.putBoolean(KEY_IS_LOGGED_IN, false)
        // 提交所有更改到SharedPreferences
        editor.apply()
        // 记录用户会话清除成功的日志
        Log.d(TAG, "User session cleared")
    }
}