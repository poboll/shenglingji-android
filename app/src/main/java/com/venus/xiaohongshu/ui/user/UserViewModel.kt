package com.venus.xiaohongshu.ui.user

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.venus.xiaohongshu.data.model.AuthResponse
import com.venus.xiaohongshu.data.model.Profile
import com.venus.xiaohongshu.data.model.User
import com.venus.xiaohongshu.data.repository.UserRepository
import com.venus.xiaohongshu.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository = UserRepository()
    private val sessionManager = SessionManager(application)
    private val context = application
    private val TAG = "UserViewModel"
    
    // LiveData
    private val _loginResult = MutableLiveData<Result<AuthResponse>>()
    val loginResult: LiveData<Result<AuthResponse>> = _loginResult
    
    private val _registerResult = MutableLiveData<Result<AuthResponse>>()
    val registerResult: LiveData<Result<AuthResponse>> = _registerResult
    
    private val _currentUser = MutableLiveData<Result<User>>()
    val currentUser: LiveData<Result<User>> = _currentUser
    
    private val _updateUserResult = MutableLiveData<Result<User>>()
    val updateUserResult: LiveData<Result<User>> = _updateUserResult
    
    private val _updateProfileResult = MutableLiveData<Result<Profile>>()
    val updateProfileResult: LiveData<Result<Profile>> = _updateProfileResult
    
    private val _changePasswordResult = MutableLiveData<Result<String>>()
    val changePasswordResult: LiveData<Result<String>> = _changePasswordResult
    
    // 登录
    fun login(username: String, password: String) {
        viewModelScope.launch {
            val result = userRepository.login(username, password)
            _loginResult.value = result
            
            if (result.isSuccess) {
                result.getOrNull()?.let {
                    sessionManager.saveAuthUser(it.token, it.user)
                }
            }
        }
    }
    
    // 注册
    fun register(username: String, email: String, password: String, nickname: String? = null, phone: String? = null) {
        viewModelScope.launch {
            val result = userRepository.register(username, email, password, nickname, phone)
            _registerResult.value = result
            
            if (result.isSuccess) {
                result.getOrNull()?.let {
                    sessionManager.saveAuthUser(it.token, it.user)
                }
            }
        }
    }
    
    // 获取当前用户信息
    fun fetchCurrentUser() {
        val token = sessionManager.getToken() ?: return
        
        viewModelScope.launch {
            val result = userRepository.getCurrentUser(token)
            _currentUser.value = result
            
            if (result.isSuccess) {
                result.getOrNull()?.let { sessionManager.updateUser(it) }
            }
        }
    }
    
    // 更新用户基本信息
    fun updateUser(
        nickname: String? = null,
        bio: String? = null,
        avatar: String? = null,
        gender: String? = null,
        birthday: String? = null,
        phone: String? = null
    ) {
        val token = sessionManager.getToken() ?: return
        
        viewModelScope.launch {
            val result = userRepository.updateUser(token, nickname, bio, avatar, gender, birthday, phone)
            _updateUserResult.value = result
            
            if (result.isSuccess) {
                result.getOrNull()?.let { sessionManager.updateUser(it) }
            }
        }
    }
    
    // 更新用户资料详情
    fun updateProfile(
        location: String? = null,
        website: String? = null,
        education: String? = null,
        occupation: String? = null,
        interests: String? = null,
        socialLinks: Map<String, String>? = null,
        preferences: Map<String, Any>? = null
    ) {
        val token = sessionManager.getToken() ?: return
        
        viewModelScope.launch {
            val result = userRepository.updateProfile(token, location, website, education, occupation, interests, socialLinks, preferences)
            _updateProfileResult.value = result
        }
    }
    
    // 修改密码
    fun changePassword(currentPassword: String, newPassword: String) {
        val token = sessionManager.getToken() ?: return
        
        viewModelScope.launch {
            val result = userRepository.changePassword(token, currentPassword, newPassword)
            _changePasswordResult.value = result
        }
    }
    
    // 获取随机头像
    fun fetchRandomAvatar(callback: (String?) -> Unit) {
        viewModelScope.launch {
            Log.d(TAG, "[fetchRandomAvatar] 开始获取随机头像")
            val result = userRepository.getRandomAvatar()
            withContext(Dispatchers.Main) {
                val avatarUrl = result.getOrNull()
                Log.d(TAG, "[fetchRandomAvatar] UserRepository 返回结果: isSuccess=${result.isSuccess}, avatarUrl='$avatarUrl'")
                if (result.isSuccess && !avatarUrl.isNullOrEmpty()) {
                    Log.d(TAG, "[fetchRandomAvatar] 成功，回调 URL: $avatarUrl")
                    callback(avatarUrl)
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "URL为空或获取失败"
                    Log.e(TAG, "[fetchRandomAvatar] 失败: $errorMsg")
                    callback(null)
                }
            }
        }
    }
    
    // 上传头像
    fun uploadAvatar(imageUri: Uri, callback: (String?) -> Unit) {
        viewModelScope.launch {
            Log.d(TAG, "[uploadAvatar] 开始上传头像, Uri: $imageUri")
            try {
                val file = withContext(Dispatchers.IO) {
                    val inputStream = context.contentResolver.openInputStream(imageUri)
                    val tempFile = File(context.cacheDir, "avatar_upload.jpg") // 确保文件名一致性或动态生成
                    inputStream?.use { input ->
                        FileOutputStream(tempFile).use { output ->
                            input.copyTo(output)
                        }
                    } ?: throw IOException("无法打开输入流 for URI: $imageUri")
                    Log.d(TAG, "[uploadAvatar] 图片已复制到临时文件: ${tempFile.absolutePath}, 大小: ${tempFile.length()}")
                    tempFile
                }
                
                val token = sessionManager.getToken()
                if (token.isNullOrEmpty()) {
                    Log.e(TAG, "[uploadAvatar] Token 为空，无法上传")
                    callback(null)
                    return@launch
                }

                val result = userRepository.uploadAvatar(token, file)
                
                withContext(Dispatchers.Main) {
                    val uploadedUrl = result.getOrNull()
                    Log.d(TAG, "[uploadAvatar] UserRepository 返回结果: isSuccess=${result.isSuccess}, uploadedUrl='$uploadedUrl'")
                    if (result.isSuccess && !uploadedUrl.isNullOrEmpty()) {
                        Log.d(TAG, "[uploadAvatar] 成功，回调 URL: $uploadedUrl")
                        callback(uploadedUrl)
                    } else {
                        val errorMsg = result.exceptionOrNull()?.message ?: "上传失败或URL为空"
                        Log.e(TAG, "[uploadAvatar] 失败: $errorMsg")
                        callback(null)
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "[uploadAvatar] 处理图片文件时出错: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    callback(null)
                }
            }
        }
    }
    
    // 获取随机昵称
    fun fetchRandomNickname(callback: (String?) -> Unit) {
        viewModelScope.launch {
            val result = userRepository.getRandomNickname()
            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    callback(result.getOrNull())
                } else {
                    Log.e(TAG, "获取随机昵称失败: ${result.exceptionOrNull()?.message}")
                    callback(null)
                }
            }
        }
    }
    
    // 检查用户是否已登录
    fun isLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }
    
    // 获取已保存的用户信息
    fun getSavedUser(): User? {
        return sessionManager.getUser()
    }
    
    // 注销
    fun logout() {
        sessionManager.logout()
    }
} 