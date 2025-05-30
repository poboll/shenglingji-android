package com.venus.xiaohongshu.data.model

import com.google.gson.annotations.SerializedName

data class PostResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: PostListData
)

data class PostListData(
    @SerializedName("total") val total: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("posts") val posts: List<Post>
) 