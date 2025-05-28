package com.caiths.shenglingji.ui.mine.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caiths.shenglingji.ui.mine.model.ApiResponse
import com.caiths.shenglingji.ui.mine.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Description: 身份验证ViewModel，处理登录和注册
 *
 * @author: venus
 * @date: 2024/11/30
 */
class AuthViewModel : ViewModel() {
    
    private val _loginResult = MutableStateFlow<ApiResponse<User>?>(null)
    val loginResult: StateFlow<ApiResponse<User>?> = _loginResult.asStateFlow()
    
    private val _registerResult = MutableStateFlow<ApiResponse<User>?>(null)
    val registerResult: StateFlow<ApiResponse<User>?> = _registerResult.asStateFlow()
    
    /**
     * 用户登录
     */
    fun login(username: String, password: String) {
        viewModelScope.launch {
            // 模拟登录成功
            _loginResult.value = ApiResponse(
                success = true,
                message = "登录成功",
                data = User(
                    id = 1,
                    username = username,
                    email = "$username@example.com",
                    nickname = username,
                    avatar = null
                )
            )
        }
    }
    
    /**
     * 用户注册
     */
    fun register(username: String, email: String, password: String, nickname: String? = null) {
        viewModelScope.launch {
            // 模拟注册成功
            _registerResult.value = ApiResponse(
                success = true,
                message = "注册成功",
                data = User(
                    id = 1,
                    username = username,
                    email = email,
                    nickname = nickname ?: username,
                    avatar = null
                )
            )
        }
    }
    
    /**
     * 检查用户是否已登录
     */
    fun isLoggedIn(): Boolean {
        return false // 始终返回未登录状态，便于测试登录流程
    }
    
    /**
     * 获取当前登录用户
     */
    fun getCurrentUser(): User? {
        return null
    }
    
    /**
     * 退出登录
     */
    fun logout(callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            callback(true)
        }
    }
} 