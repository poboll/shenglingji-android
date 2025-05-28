package com.caiths.shenglingji.ui.home.bean

/**
 * 图文卡片数据模型
 */
data class GraphicCardBean(
    val id: String,
    val title: String,
    val imageUrl: Int,
    val type: GraphicCardType = GraphicCardType.NORMAL
)

/**
 * 卡片类型
 */
enum class GraphicCardType {
    NORMAL,
    VIDEO,
    ARTICLE
}
