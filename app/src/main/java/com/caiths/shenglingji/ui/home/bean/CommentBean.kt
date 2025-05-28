package com.caiths.shenglingji.ui.home.bean

/**
 * 评论数据模型
 */
data class CommentBean(
    val id: String,
    val title: String,
    val user: UserBean,
    val likes: Int = 0
) 