package com.venus.xiaohongshu.ui.home.bean

/**
 * 帖子数据类
 */
data class PostData(
    val id: Int,
    val title: String,
    val content: String?,
    val likes: Int,
    val comments: Int,
    val views: Int,
    val type: Int, // 类型: 1=植物, 2=动物
    val isHot: Boolean,
    val location: String?,
    val createdAt: String,
    val updatedAt: String,
    val author: AuthorData,
    val images: List<PostImageData>?,
    val videos: List<PostVideoData>?
)

/**
 * 帖子作者数据类
 */
data class AuthorData(
    val id: Int,
    val username: String,
    val avatar: String?
)

/**
 * 帖子图片数据类
 */
data class PostImageData(
    val id: Int,
    val imageUrl: String,
    val position: Int,
    val description: String?
)

/**
 * 帖子视频数据类
 */
data class PostVideoData(
    val id: Int,
    val videoUrl: String,
    val coverUrl: String?,
    val duration: Int?
)

/**
 * 帖子数据转换为图文卡片Bean
 */
fun PostData.toGraphicCardBean(): GraphicCardBean {
    // 确定卡片类型
    val cardType = if (videos?.isNotEmpty() == true) {
        GraphicCardType.Video
    } else {
        GraphicCardType.Graphic
    }
    
    // 创建用户Bean
    val userBean = UserBean(
        id = author.id.toString(),
        name = author.username,
        // 这里需要将URL转换为本地资源ID，实际情况下应该使用图片加载库如Glide或Coil
        // 这里暂时使用默认图片
        image = com.venus.xiaohongshu.R.drawable.icon_mine
    )
    
    return GraphicCardBean(
        id = id.toString(),
        title = title,
        // 这里同样需要处理图片URL，暂时使用默认图片
        image = com.venus.xiaohongshu.R.drawable.icon_logo,
        // 视频暂时也使用默认资源
        video = 0,
        user = userBean,
        likes = likes,
        type = cardType
    )
} 