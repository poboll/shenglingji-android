package com.caiths.shenglingji.ui.home.bean

import com.caiths.shenglingji.R

/**
 * Description: 用户bean
 *
 * @author: venus
 * @date: 2024/11/15
 */
data class UserBean(
    val id: String,
    val name: String,
    val image: Int
)

data class UserInfoBean(
    val age: Int = 0,
    val sex: Int = 0,
    val address: String = ""
)