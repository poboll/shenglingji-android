// 定义包名，标识API相关类的命名空间
package com.venus.xiaohongshu.api

/**
 * API响应通用数据类
 * 用于封装所有API请求的标准响应格式
 * 提供统一的成功/失败状态、消息和数据结构
 * @param T 响应数据的泛型类型
 */
data class ApiResponse<T>(
    // 请求是否成功的标识，true表示成功，false表示失败
    val success: Boolean,
    // 响应消息，通常包含成功提示或错误描述，可选字段
    val message: String? = null,
    // 响应的具体数据，泛型类型，可选字段
    val data: T? = null,
    // 错误信息，当请求失败时提供详细的错误描述，可选字段
    val error: String? = null
)

/**
 * 分页数据通用类
 * 用于封装需要分页显示的数据列表
 * 提供分页相关的元数据信息
 * @param T 列表项的泛型类型
 */
data class PagedData<T>(
    // 数据总数，表示所有页面的数据总量
    val total: Int,
    // 总页数，根据每页数量和总数计算得出
    val totalPages: Int,
    // 当前页码，从1开始计数
    val currentPage: Int,
    // 当前页的数据列表，泛型类型的列表
    val posts: List<T>
)