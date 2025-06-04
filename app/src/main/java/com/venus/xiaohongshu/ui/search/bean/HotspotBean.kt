package com.venus.xiaohongshu.ui.search.bean

/**
 * Description:
 *
 * @author: poboll
 * @date: 2024/05/31
 */
data class HotspotBean(
    val id: String,
    val title: String,
    val heat: Double, // 热度
    val label: String = "", // 标签
)
