package com.venus.xiaohongshu.data

/**
 * UserBean class for compatibility with the CityViewModel
 */
data class UserBean(
    val id: String,
    val name: String = "",
    val username: String = "",
    val avatar: String? = null,
    val userInfo: String? = null
) 