package com.venus.xiaohongshu.data

import com.google.gson.annotations.SerializedName
import com.venus.xiaohongshu.utils.NetworkUtils

/**
 * 帖子列表响应
 */
data class PostsResponse(
    val success: Boolean,
    val data: PostsData
)

/**
 * 帖子列表数据
 */
data class PostsData(
    val total: Int,
    val totalPages: Int,
    val currentPage: Int,
    val posts: List<Post>
)

/**
 * 帖子详情响应
 */
data class PostDetailResponse(
    val success: Boolean,
    val data: Post
)

/**
 * 评论响应
 */
data class CommentResponse(
    val success: Boolean,
    val data: List<Comment>
)

/**
 * 帖子模型
 */
data class Post(
    val id: String,
    val title: String,
    val content: String?,
    val type: Int?, // 帖子类型：1-植物，2-动物
    val mediaType: String? = "image", // 媒体类型：image-图片，video-视频
    val location: String?,
    val coverImage: String? = null, // 封面图片URL
    val mediaUrl: String? = null, // 媒体URL
    val likes: Int = 0,
    val views: Int = 0,
    val comments: Int = 0,
    val author: Author,
    val images: List<PostImage>? = null,
    val videos: List<PostVideo>? = null,
    val commentsList: List<Comment>? = null, // 评论列表
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    val isHot: Boolean = false
) {
    // 获取显示用的封面图片
    fun getDisplayCover(): String? {
        val url = coverImage 
            ?: videos?.firstOrNull()?.coverUrl 
            ?: images?.firstOrNull()?.imageUrl 
            ?: mediaUrl
        return NetworkUtils.convertLocalhostUrl(url)
    }
    
    // 获取主要媒体URL
    fun getMediaFileUrl(): String? {
        val url = mediaUrl 
            ?: if (mediaType == "video") videos?.firstOrNull()?.videoUrl
            else images?.firstOrNull()?.imageUrl
        return NetworkUtils.convertLocalhostUrl(url)
    }
    
    // 判断是否为视频帖子
    fun isVideoPost(): Boolean {
        return mediaType == "video" || videos?.isNotEmpty() == true
    }
}

/**
 * 作者信息
 */
data class Author(
    val id: String,
    val username: String,
    @SerializedName("avatar") private val _avatar: String?
) {
    val avatar: String?
        get() = NetworkUtils.convertLocalhostUrl(_avatar)
}

/**
 * 帖子图片
 */
data class PostImage(
    val id: String,
    @SerializedName("imageUrl") private val _imageUrl: String,
    val position: Int,
    val description: String?
) {
    val imageUrl: String
        get() = NetworkUtils.convertLocalhostUrl(_imageUrl) ?: _imageUrl
}

/**
 * 帖子视频
 */
data class PostVideo(
    val id: String,
    @SerializedName("videoUrl") private val _videoUrl: String,
    @SerializedName("coverUrl") private val _coverUrl: String?,
    val duration: Int?
) {
    val videoUrl: String
        get() = NetworkUtils.convertLocalhostUrl(_videoUrl) ?: _videoUrl
    
    val coverUrl: String?
        get() = NetworkUtils.convertLocalhostUrl(_coverUrl)
}

/**
 * 用户模型（兼容旧版本）
 */
data class User(
    val id: String,
    val name: String,
    @SerializedName("user_name") val userName: String?,
    @SerializedName("user_avatar") val userAvatar: String?,
    val image: String? // 兼容旧字段
)

/**
 * 评论模型
 */
data class Comment(
    val id: String,
    @SerializedName("post_id") val postId: String,
    val title: String,
    val content: String?,
    val likes: Int,
    val user: User,
    @SerializedName("created_at") val createdAt: String
)