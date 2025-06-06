package com.venus.xiaohongshu.ui.message.bean

import com.venus.xiaohongshu.ui.home.bean.UserBean

/**
 * Description:
 *
 * @author: poboll
 * @date: 2024/05/30
 */
data class RecommendFriendBean(
    val id: String,
    val user: UserBean,
    val reason: String, //推荐理由
)