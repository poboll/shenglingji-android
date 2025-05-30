package com.venus.xiaohongshu.data.repository

import android.util.Log
import com.venus.xiaohongshu.data.api.RetrofitModule
import com.venus.xiaohongshu.data.model.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UserRepository {
    private val api = RetrofitModule.userApiService
    private val TAG = "UserRepository"
    
    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return try {
            Log.d(TAG, "尝试登录: 用户名: $username")
            val request = LoginRequest(username, password)
            val response = api.login(request)
            
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "登录成功: ${response.body()}")
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "登录失败"
                Log.e(TAG, "登录失败: HTTP ${response.code()}, 错误: $errorBody")
                Result.failure(Exception(errorBody))
            }
        } catch (e: Exception) {
            Log.e(TAG, "登录异常: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun register(
        username: String,
        email: String,
        password: String,
        nickname: String? = null,
        phone: String? = null
    ): Result<AuthResponse> {
        return try {
            Log.d(TAG, "尝试注册: 用户名: $username, 邮箱: $email")
            val request = RegisterRequest(username, email, password, nickname, phone)
            val response = api.register(request)
            
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "注册成功: ${response.body()}")
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "注册失败"
                Log.e(TAG, "注册失败: HTTP ${response.code()}, 错误: $errorBody")
                Result.failure(Exception(errorBody))
            }
        } catch (e: Exception) {
            Log.e(TAG, "注册异常: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun getCurrentUser(token: String): Result<User> {
        return try {
            Log.d(TAG, "获取当前用户信息")
            val response = api.getCurrentUser("Bearer $token")
            
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "获取用户信息成功: ${response.body()}")
                Result.success(response.body()!!["user"]!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "获取用户信息失败"
                Log.e(TAG, "获取用户信息失败: HTTP ${response.code()}, 错误: $errorBody")
                Result.failure(Exception(errorBody))
            }
        } catch (e: Exception) {
            Log.e(TAG, "获取用户信息异常: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateUser(
        token: String,
        nickname: String? = null,
        bio: String? = null,
        avatar: String? = null,
        gender: String? = null,
        birthday: String? = null,
        phone: String? = null
    ): Result<User> {
        return try {
            Log.d(TAG, "更新用户信息: nickname=$nickname, bio=$bio, avatar=$avatar")
            val request = UpdateUserRequest(nickname, bio, avatar, gender, birthday, phone)
            val response = api.updateUser("Bearer $token", request)
            
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "更新用户信息成功: ${response.body()}")
                Result.success(response.body()!!["user"]!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "更新用户信息失败"
                Log.e(TAG, "更新用户信息失败: HTTP ${response.code()}, 错误: $errorBody")
                Result.failure(Exception(errorBody))
            }
        } catch (e: Exception) {
            Log.e(TAG, "更新用户信息异常: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateProfile(
        token: String,
        location: String? = null,
        website: String? = null,
        education: String? = null,
        occupation: String? = null,
        interests: String? = null,
        socialLinks: Map<String, String>? = null,
        preferences: Map<String, Any>? = null
    ): Result<Profile> {
        return try {
            Log.d(TAG, "更新用户资料: location=$location, website=$website")
            val request = UpdateProfileRequest(location, website, education, occupation, interests, socialLinks, preferences)
            val response = api.updateProfile("Bearer $token", request)
            
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "更新用户资料成功: ${response.body()}")
                Result.success(response.body()!!["profile"]!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "更新用户资料失败"
                Log.e(TAG, "更新用户资料失败: HTTP ${response.code()}, 错误: $errorBody")
                Result.failure(Exception(errorBody))
            }
        } catch (e: Exception) {
            Log.e(TAG, "更新用户资料异常: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun changePassword(token: String, currentPassword: String, newPassword: String): Result<String> {
        return try {
            Log.d(TAG, "修改密码")
            val request = ChangePasswordRequest(currentPassword, newPassword)
            val response = api.changePassword("Bearer $token", request)
            
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "修改密码成功")
                Result.success(response.body()!!["message"] ?: "密码修改成功")
            } else {
                val errorBody = response.errorBody()?.string() ?: "修改密码失败"
                Log.e(TAG, "修改密码失败: HTTP ${response.code()}, 错误: $errorBody")
                Result.failure(Exception(errorBody))
            }
        } catch (e: Exception) {
            Log.e(TAG, "修改密码异常: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // 上传头像
    suspend fun uploadAvatar(token: String, file: File): Result<String> {
        return try {
            Log.d(TAG, "上传头像: ${file.name}, 大小: ${file.length()}")
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull()) // 明确指定JPG类型
            val body = MultipartBody.Part.createFormData("avatar", file.name, requestFile)
            
            val response = api.uploadAvatar("Bearer $token", body)
            Log.d(TAG, "上传头像响应: Code=${response.code()}, Body=${response.body()}, ErrorBody=${response.errorBody()?.string()}")
            
            if (response.isSuccessful && response.body() != null) {
                val avatarUrl = response.body()!!["avatarUrl"]
                Log.d(TAG, "上传头像成功，URL: $avatarUrl")
                Result.success(avatarUrl ?: "")
            } else {
                val errorMsg = response.errorBody()?.string() ?: "上传头像响应失败但没有错误体"
                Log.e(TAG, "上传头像失败: HTTP ${response.code()}, 错误: $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "上传头像异常: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // 获取随机头像
    suspend fun getRandomAvatar(): Result<String> {
        return try {
            Log.d(TAG, "[getRandomAvatar] 尝试获取随机头像")
            val response = api.getRandomAvatar()
            Log.d(TAG, "[getRandomAvatar] 响应: Code=${response.code()}, IsSuccessful=${response.isSuccessful}, Body=${response.body()}")
            
            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!
                val avatarUrl = responseBody["avatarUrl"]
                Log.d(TAG, "[getRandomAvatar] 原始avatarUrl: '$avatarUrl' (类型: ${avatarUrl?.javaClass?.name})")
                if (avatarUrl.isNullOrEmpty()) {
                    Log.w(TAG, "[getRandomAvatar] 后端返回的avatarUrl为空或null")
                    Result.failure(Exception("后端返回的avatarUrl为空"))
                } else {
                    Log.d(TAG, "[getRandomAvatar] 获取随机头像成功，URL: $avatarUrl")
                    Result.success(avatarUrl)
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "获取随机头像失败，无错误体"
                Log.e(TAG, "[getRandomAvatar] 获取随机头像失败: HTTP ${response.code()}, 错误: $errorBody")
                Result.failure(Exception(errorBody))
            }
        } catch (e: Exception) {
            Log.e(TAG, "[getRandomAvatar] 获取随机头像异常: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // 获取随机昵称
    suspend fun getRandomNickname(): Result<String> {
        return try {
            Log.d(TAG, "获取随机昵称")
            val response = api.getRandomNickname()
            
            if (response.isSuccessful && response.body() != null) {
                val nickname = response.body()!!["nickname"]
                Log.d(TAG, "获取随机昵称成功: $nickname")
                Result.success(nickname ?: "")
            } else {
                val errorBody = response.errorBody()?.string() ?: "获取随机昵称失败"
                Log.e(TAG, "获取随机昵称失败: HTTP ${response.code()}, 错误: $errorBody")
                Result.failure(Exception(errorBody))
            }
        } catch (e: Exception) {
            Log.e(TAG, "获取随机昵称异常: ${e.message}", e)
            Result.failure(e)
        }
    }
} 