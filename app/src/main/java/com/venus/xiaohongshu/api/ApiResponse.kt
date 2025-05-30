package com.venus.xiaohongshu.api

/**
 * API响应通用数据类
 */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val error: String? = null
)

/**
 * 分页数据类
 */
data class PagedData<T>(
    val total: Int,
    val totalPages: Int,
    val currentPage: Int,
    val posts: List<T>
) 