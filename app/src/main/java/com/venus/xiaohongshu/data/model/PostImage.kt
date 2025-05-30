package com.venus.xiaohongshu.data.model

import com.google.gson.annotations.SerializedName

data class PostImage(
    @SerializedName("id") val id: String,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("position") val position: Int,
    @SerializedName("description") val description: String? = null // Added from postDetail API
) 