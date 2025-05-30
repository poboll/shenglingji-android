package com.venus.xiaohongshu.data.model

import com.google.gson.annotations.SerializedName

data class Post(
    val id: String,
    val title: String,
    val content: String?,
    val type: PostType?, // 改为可空类型
    val category: String?,
    val coverImage: String?, // 封面图片（视频帖子使用）
    val mediaUrl: String?, // 媒体文件URL（图片或视频）
    val likes: Int,
    val views: Int,
    val author: User,
    val images: List<PostImage>?,
    val videos: List<PostVideo>?,
    val createdAt: String,
    val updatedAt: String
) {
    // 获取显示用的封面图片
    fun getDisplayCover(): String? {
        return when (type) {
            PostType.VIDEO -> coverImage ?: videos?.firstOrNull()?.videoUrl
            PostType.IMAGE -> images?.firstOrNull()?.imageUrl ?: mediaUrl
            null -> {
                // 当type为null时，优先使用coverImage，然后尝试视频封面，最后使用图片
                coverImage 
                    ?: videos?.firstOrNull()?.let { it.coverUrl ?: it.videoUrl }
                    ?: images?.firstOrNull()?.imageUrl 
                    ?: mediaUrl
            }
        }
    }
    
    // 获取媒体文件URL
    fun getVideoMediaUrl(): String? {
        return when (type) {
            PostType.VIDEO -> videos?.firstOrNull()?.videoUrl ?: mediaUrl
            PostType.IMAGE -> images?.firstOrNull()?.imageUrl ?: mediaUrl
            null -> {
                // 当type为null时，优先尝试视频URL，然后使用图片URL
                videos?.firstOrNull()?.videoUrl 
                    ?: images?.firstOrNull()?.imageUrl 
                    ?: mediaUrl
            }
        }
    }
    
    // 判断是否为视频帖子
    fun isVideoPost(): Boolean {
        return type == PostType.VIDEO || videos?.isNotEmpty() == true
    }
}

enum class PostType {
    IMAGE, VIDEO
}