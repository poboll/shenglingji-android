package com.venus.xiaohongshu.ui.home.bean

import com.venus.xiaohongshu.R
import com.venus.xiaohongshu.data.model.Post as OldPost
import com.venus.xiaohongshu.data.Post
import java.util.UUID

/**
 * Description: 图文卡片bean
 *
 * @author: venus
 * @date: 2024/11/15
 */
data class GraphicCardBean(
    val id: String = "",
    val title: String = "",
    val image: Int = R.drawable.icon_logo,
    val imageUrl: String? = null,  // 添加imageUrl字段支持URL图片
    val video: Int = 0,
    val videoUrl: String? = null,  // 添加videoUrl字段支持URL视频
    val user: UserBean,
    val likes: Int = 0,
    val type: GraphicCardType = GraphicCardType.Graphic
) {
    companion object {
        // 从旧的Post模型转换为GraphicCardBean
        fun fromPost(post: OldPost): GraphicCardBean {
            // 判断是否含有视频，决定卡片类型
            // 优先使用post.type字段，如果为null则根据videos判断
            val hasVideo = when (post.type) {
                com.venus.xiaohongshu.data.model.PostType.VIDEO -> true
                com.venus.xiaohongshu.data.model.PostType.IMAGE -> false
                null -> post.videos?.isNotEmpty() == true || post.isVideoPost()
            }
            val cardType = if (hasVideo) GraphicCardType.Video else GraphicCardType.Graphic
            
            // 创建用户Bean
            val user = UserBean(
                id = post.id,
                name = post.author.username ?: "用户",
                userName = post.author.username,
                userAvatar = post.author.avatar
            )
            
            // 获取视频URL或者覆盖图像URL
            val videoUrl = if (hasVideo) {
                post.videos?.firstOrNull()?.videoUrl ?: post.getVideoMediaUrl()
            } else null
            
            return GraphicCardBean(
                id = post.id,
                title = post.title,
                imageUrl = post.getDisplayCover(),
                videoUrl = videoUrl,
                user = user,
                likes = post.likes,
                type = cardType
            )
        }
        
        // 从新的Post模型转换为GraphicCardBean
        fun fromPost(post: Post): GraphicCardBean {
            // 判断是否含有视频，决定卡片类型
            val isVideo = post.mediaType == "video" || post.videos?.isNotEmpty() == true
            val cardType = if (isVideo) GraphicCardType.Video else GraphicCardType.Graphic
            
            // 创建用户Bean
            val user = UserBean(
                id = post.author.id,
                name = post.author.username,
                userName = post.author.username,
                userAvatar = post.author.avatar
            )
            
            // 优先使用视频URL，其次使用mediaUrl
            val videoUrl = if (isVideo) {
                post.videos?.firstOrNull()?.videoUrl ?: post.mediaUrl
            } else null
            
            return GraphicCardBean(
                id = post.id,
                title = post.title,
                imageUrl = post.getDisplayCover(),
                videoUrl = videoUrl,
                user = user,
                likes = post.likes,
                type = cardType
            )
        }
    }
}

enum class GraphicCardType {
    Graphic, // 图片帖子
    Video    // 视频帖子
}
