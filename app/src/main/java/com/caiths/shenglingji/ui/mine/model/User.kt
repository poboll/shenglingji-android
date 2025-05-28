package com.caiths.shenglingji.ui.mine.model

/**
 * 用户信息模型
 */
data class User(
    val id: Int,
    val username: String,
    val email: String,
    val nickname: String? = null,
    val avatar: String? = null
)

/**
 * API响应封装类
 */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
) 