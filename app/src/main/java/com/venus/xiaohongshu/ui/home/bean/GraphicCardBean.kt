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
            val hasVideo = post.videos?.isNotEmpty() == true
            val cardType = if (hasVideo) GraphicCardType.Video else GraphicCardType.Graphic
            
            // 创建用户Bean
            val user = UserBean(
                id = post.userId.toString(),
                name = post.getAuthorUsername() ?: "用户",
                userName = post.getAuthorUsername(),
                userAvatar = post.getAuthorAvatar()
            )
            
            return GraphicCardBean(
                id = post.id.toString(),
                title = post.title,
                imageUrl = post.getDisplayCover(),
                videoUrl = post.videos?.firstOrNull()?.videoUrl,
                user = user,
                likes = post.getLikeDisplayCount(),
                type = cardType
            )
        }
        
        // 从新的Post模型转换为GraphicCardBean
        fun fromPost(post: Post): GraphicCardBean {
            // 判断是否含有视频，决定卡片类型
            val hasVideo = post.videos?.isNotEmpty() == true
            val cardType = if (hasVideo) GraphicCardType.Video else GraphicCardType.Graphic
            
            // 创建用户Bean
            val user = UserBean(
                id = post.author.id,
                name = post.author.username,
                userName = post.author.username,
                userAvatar = post.author.avatar
            )
            
            return GraphicCardBean(
                id = post.id,
                title = post.title,
                imageUrl = post.getDisplayCover(),
                videoUrl = post.videos?.firstOrNull()?.videoUrl,
                user = user,
                likes = post.likes,
                type = cardType
            )
        }
    }
}

enum class GraphicCardType() {
    Graphic, // 图文类型
    Video, // 视频类型
}
