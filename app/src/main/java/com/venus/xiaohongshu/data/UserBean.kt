// 定义包名，标识数据模型类的命名空间
package com.venus.xiaohongshu.data

/**
 * 用户Bean数据类
 * 为了与CityViewModel保持兼容性而创建的用户数据模型
 * 提供简化的用户信息结构
 */
data class UserBean(
    // 用户的唯一标识符
    val id: String,
    // 用户显示名称，默认为空字符串
    val name: String = "",
    // 用户名，默认为空字符串
    val username: String = "",
    // 用户头像URL，可选字段，默认为null
    val avatar: String? = null,
    // 用户详细信息，可选字段，默认为null
    val userInfo: String? = null
)