package com.venus.xiaohongshu.ui.message.bean

import com.venus.xiaohongshu.ui.home.bean.UserBean

/**
 * Description:
 *
 * @author: poboll
 * @date: 2024/06/02
 */
data class MessageBean(
    val id: String,
    val user: UserBean,
    val title: String,
    val content: String,
    val time: String,
    val new: Boolean = false, // 新消息提醒
)
