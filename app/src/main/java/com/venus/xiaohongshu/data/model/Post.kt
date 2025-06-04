// 定义包名，标识数据模型类的命名空间
package com.venus.xiaohongshu.data.model

// 导入Gson序列化注解，用于JSON字段映射
import com.google.gson.annotations.SerializedName

/**
 * 帖子数据类
 * 用于封装社交媒体平台中的帖子信息，支持图片和视频两种类型
 * 包含帖子的基本信息、媒体内容、互动数据和作者信息
 * 
 * @property id 帖子唯一标识符
 * @property title 帖子标题
 * @property content 帖子内容，可为空
 * @property type 帖子类型（图片或视频），可为空以兼容旧数据
 * @property category 帖子分类，可为空
 * @property coverImage 封面图片URL，主要用于视频帖子的封面展示
 * @property mediaUrl 媒体文件URL，可以是图片或视频的直接链接
 * @property likes 点赞数量
 * @property views 浏览次数
 * @property author 帖子作者信息
 * @property images 图片列表，用于多图帖子
 * @property videos 视频列表，用于视频帖子
 * @property createdAt 创建时间字符串
 * @property updatedAt 更新时间字符串
 */
data class Post(
    val id: String,
    val title: String,
    val content: String?,
    val type: PostType?, // 改为可空类型以兼容旧数据格式
    val category: String?,
    val coverImage: String?, // 封面图片URL（主要用于视频帖子）
    val mediaUrl: String?, // 媒体文件URL（图片或视频的直接链接）
    val likes: Int,
    val views: Int,
    val author: User,
    val images: List<PostImage>?,
    val videos: List<PostVideo>?,
    val createdAt: String,
    val updatedAt: String
) {
    /**
     * 获取用于显示的封面图片URL
     * 根据帖子类型智能选择最合适的封面图片
     * 
     * @return 封面图片URL，如果没有可用的封面则返回null
     */
    fun getDisplayCover(): String? {
        return when (type) {
            // 视频帖子：优先使用专门的封面图片，否则使用视频URL作为封面
            PostType.VIDEO -> coverImage ?: videos?.firstOrNull()?.videoUrl
            // 图片帖子：优先使用图片列表中的第一张，否则使用通用媒体URL
            PostType.IMAGE -> images?.firstOrNull()?.imageUrl ?: mediaUrl
            // 类型未知时的兼容处理：按优先级依次尝试各种可能的封面源
            null -> {
                // 当type为null时，优先使用coverImage，然后尝试视频封面，最后使用图片
                coverImage 
                    ?: videos?.firstOrNull()?.let { it.coverUrl ?: it.videoUrl }
                    ?: images?.firstOrNull()?.imageUrl 
                    ?: mediaUrl
            }
        }
    }
    
    /**
     * 获取视频媒体文件的URL
     * 用于视频播放或媒体文件访问
     * 
     * @return 媒体文件URL，如果没有可用的媒体文件则返回null
     */
    fun getVideoMediaUrl(): String? {
        return when (type) {
            // 视频帖子：优先使用视频列表中的第一个视频，否则使用通用媒体URL
            PostType.VIDEO -> videos?.firstOrNull()?.videoUrl ?: mediaUrl
            // 图片帖子：优先使用图片列表中的第一张，否则使用通用媒体URL
            PostType.IMAGE -> images?.firstOrNull()?.imageUrl ?: mediaUrl
            // 类型未知时的兼容处理：按优先级依次尝试各种可能的媒体源
            null -> {
                // 当type为null时，优先尝试视频URL，然后使用图片URL
                videos?.firstOrNull()?.videoUrl 
                    ?: images?.firstOrNull()?.imageUrl 
                    ?: mediaUrl
            }
        }
    }
    
    /**
     * 判断当前帖子是否为视频类型
     * 通过帖子类型或视频列表的存在性来判断
     * 
     * @return true表示是视频帖子，false表示不是视频帖子
     */
    fun isVideoPost(): Boolean {
        // 如果明确标记为视频类型，或者视频列表不为空，则认为是视频帖子
        return type == PostType.VIDEO || videos?.isNotEmpty() == true
    }
}

/**
 * 帖子类型枚举
 * 定义社交媒体平台支持的帖子内容类型
 */
enum class PostType {
    // 图片类型帖子，包含一张或多张图片
    IMAGE, 
    // 视频类型帖子，包含视频内容
    VIDEO
}