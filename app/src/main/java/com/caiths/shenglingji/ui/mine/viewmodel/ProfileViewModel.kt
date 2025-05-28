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
 * Description: 个人资料ViewModel，处理用户信息更新
 *
 * @author: venus
 * @date: 2024/11/30
 */
class ProfileViewModel : ViewModel() {
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _updateResult = MutableStateFlow<ApiResponse<User>?>(null)
    val updateResult: StateFlow<ApiResponse<User>?> = _updateResult.asStateFlow()
    
    init {
        loadCurrentUser()
    }
    
    /**
     * 加载当前登录用户
     */
    private fun loadCurrentUser() {
        // 模拟加载用户
        _currentUser.value = User(1, "模拟用户", "test@example.com", "测试昵称", null)
    }
    
    /**
     * 更新用户资料
     */
    fun updateProfile(
        nickname: String? = null,
        avatar: String? = null,
        bio: String? = null,
        interests: List<String>? = null
    ) {
        val current = _currentUser.value
        if (current == null) {
            _updateResult.value = ApiResponse(success = false, message = "用户未登录", data = null)
            return
        }

        viewModelScope.launch {
            // 模拟更新成功
            val updatedUser = current.copy(
                nickname = nickname ?: current.nickname,
                avatar = avatar ?: current.avatar
                // bio 和 interests 暂不处理，因为User模型中没有这些字段
            )
            _currentUser.value = updatedUser
            _updateResult.value = ApiResponse(success = true, message = "更新成功", data = updatedUser)
        }
    }
    
    /**
     * 刷新用户资料
     */
    fun refreshUserProfile() {
        // 模拟刷新
        loadCurrentUser()
    }
} 