package com.venus.xiaohongshu.data.model

import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String? = null,
    @SerializedName("type") val type: Int, // 1 for plant, 2 for animal
    @SerializedName("likes") val likes: Int = 0,
    @SerializedName("comments") val comments: Int = 0,
    @SerializedName("views") val views: Int = 0,
    @SerializedName("isHot") val isHot: Boolean = false,
    @SerializedName("location") val location: String? = null,
    @SerializedName("createdAt") val createdAt: String, // Keep as String, parse if needed
    @SerializedName("updatedAt") val updatedAt: String, // Keep as String, parse if needed
    @SerializedName("userId") val userId: Int,
    @SerializedName("author") val author: User, // Renamed from user to author
    @SerializedName("images") val images: List<PostImage>? = null,
    @SerializedName("videos") val videos: List<PostVideo>? = null,

    // Fields for mock data compatibility, consider removing if not needed long-term
    val cover: String? = null, // From mock data, try to map from images or videos
    val avatar: String? = null, // From mock data, available in author.avatar
    val authorName: String? = null, // From mock data, available in author.username
    val description: String? = null, // From mock data, available in content
    val likeCount: Int? = null, // From mock data, available in likes
    val isLiked: Boolean? = null // From mock data, not directly available from API
) {
    // Helper to get a cover image for display, prioritizing video cover, then first image
    fun getDisplayCover(): String? {
        return videos?.firstOrNull()?.coverUrl ?: images?.firstOrNull()?.imageUrl ?: cover
    }

    fun getAuthorAvatar(): String? {
        return author.avatar ?: avatar
    }

    fun getAuthorUsername(): String? {
        return author.username ?: authorName
    }

    fun getLikeDisplayCount(): Int {
        return likeCount ?: likes
    }
} 