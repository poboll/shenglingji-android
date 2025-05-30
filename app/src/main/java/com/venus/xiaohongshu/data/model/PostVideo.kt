package com.venus.xiaohongshu.data.model

import com.google.gson.annotations.SerializedName

data class PostVideo(
    @SerializedName("id") val id: String,
    @SerializedName("videoUrl") val videoUrl: String,
    @SerializedName("coverUrl") val coverUrl: String? = null,
    @SerializedName("duration") val duration: Int? = null
) 