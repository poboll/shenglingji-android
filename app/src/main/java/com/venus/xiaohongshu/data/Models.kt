// 定义包名，标识数据模型类的命名空间
package com.venus.xiaohongshu.data

// 导入Gson序列化注解，用于JSON字段映射
import com.google.gson.annotations.SerializedName
// 导入网络工具类，用于URL转换处理
import com.venus.xiaohongshu.utils.NetworkUtils

/**
 * 帖子列表响应数据类
 * 用于封装从服务器获取的帖子列表响应数据
 */
data class PostsResponse(
    // 请求是否成功的标识
    val success: Boolean,
    // 帖子列表的具体数据
    val data: PostsData
)

/**
 * 帖子列表数据类
 * 包含分页信息和帖子列表
 */
data class PostsData(
    // 帖子总数
    val total: Int,
    // 总页数
    val totalPages: Int,
    // 当前页码
    val currentPage: Int,
    // 帖子列表
    val posts: List<Post>
)

/**
 * 帖子详情响应数据类
 * 用于封装单个帖子的详细信息响应
 */
data class PostDetailResponse(
    // 请求是否成功的标识
    val success: Boolean,
    // 帖子详细数据
    val data: Post
)

/**
 * 评论列表响应数据类
 * 用于封装评论列表的响应数据
 */
data class CommentListResponse(
    // 请求是否成功的标识
    val success: Boolean,
    // 评论列表数据
    val data: List<Comment>
)

/**
 * 单个评论响应数据类
 * 用于封装单个评论操作的响应数据
 */
data class CommentResponse(
    // 请求是否成功的标识
    val success: Boolean,
    // 响应消息，可选字段
    val message: String? = null,
    // 评论数据
    val data: Comment
)

/**
 * 帖子数据模型类
 * 表示应用中的一个帖子，包含所有相关信息和媒体内容
 */
data class Post(
    // 帖子的唯一标识符
    val id: String,
    // 帖子标题
    val title: String,
    // 帖子内容，可选字段
    val content: String?,
    // 帖子类型：1-植物，2-动物，可选字段
    val type: Int?,
    // 媒体类型："image"-图片，"video"-视频，默认为图片
    val mediaType: String? = "image",
    // 帖子发布位置信息，可选字段
    val location: String?,
    // 封面图片URL，可选字段
    val coverImage: String? = null,
    // 主要媒体文件URL，可选字段
    val mediaUrl: String? = null,
    // 点赞数，默认为0
    val likes: Int = 0,
    // 浏览数，默认为0
    val views: Int = 0,
    // 评论数，默认为0
    val comments: Int = 0,
    // 帖子作者信息
    val author: Author,
    // 帖子包含的图片列表，可选字段
    val images: List<PostImage>? = null,
    // 帖子包含的视频列表，可选字段
    val videos: List<PostVideo>? = null,
    // 帖子的评论列表，可选字段
    val commentsList: List<Comment>? = null,
    // 帖子创建时间，使用SerializedName注解映射JSON字段
    @SerializedName("createdAt") val createdAt: String,
    // 帖子更新时间，使用SerializedName注解映射JSON字段
    @SerializedName("updatedAt") val updatedAt: String,
    // 是否为热门帖子，默认为false
    val isHot: Boolean = false
) {
    /**
     * 获取用于显示的封面图片URL
     * 优先级：封面图片 > 视频封面 > 第一张图片 > 媒体URL
     * @return 处理后的封面图片URL
     */
    fun getDisplayCover(): String? {
        // 按优先级获取封面图片URL
        val url = coverImage 
            ?: videos?.firstOrNull()?.coverUrl 
            ?: images?.firstOrNull()?.imageUrl 
            ?: mediaUrl
        // 转换localhost URL为可访问的URL
        return NetworkUtils.convertLocalhostUrl(url)
    }
    
    /**
     * 获取主要媒体文件的URL
     * 根据媒体类型返回对应的媒体文件URL
     * @return 处理后的媒体文件URL
     */
    fun getMediaFileUrl(): String? {
        // 根据媒体类型获取对应的媒体URL
        val url = mediaUrl 
            ?: if (mediaType == "video") videos?.firstOrNull()?.videoUrl
            else images?.firstOrNull()?.imageUrl
        // 转换localhost URL为可访问的URL
        return NetworkUtils.convertLocalhostUrl(url)
    }
    
    /**
     * 判断当前帖子是否为视频帖子
     * @return true表示是视频帖子，false表示不是
     */
    fun isVideoPost(): Boolean {
        // 通过媒体类型或视频列表判断是否为视频帖子
        return mediaType == "video" || videos?.isNotEmpty() == true
    }
}

/**
 * 作者信息数据类
 * 表示帖子的作者基本信息
 */
data class Author(
    // 作者的唯一标识符
    val id: String,
    // 作者用户名
    val username: String,
    // 作者头像URL，使用私有字段配合getter进行URL转换
    @SerializedName("avatar") private val _avatar: String?
) {
    /**
     * 获取处理后的头像URL
     * 自动转换localhost URL为可访问的URL
     */
    val avatar: String?
        get() = NetworkUtils.convertLocalhostUrl(_avatar)
}

/**
 * 帖子图片数据类
 * 表示帖子中包含的图片信息
 */
data class PostImage(
    // 图片的唯一标识符
    val id: String,
    // 图片URL，使用私有字段配合getter进行URL转换
    @SerializedName("imageUrl") private val _imageUrl: String,
    // 图片在帖子中的位置序号
    val position: Int,
    // 图片描述信息，可选字段
    val description: String?
) {
    /**
     * 获取处理后的图片URL
     * 自动转换localhost URL为可访问的URL，如果转换失败则返回原URL
     */
    val imageUrl: String
        get() = NetworkUtils.convertLocalhostUrl(_imageUrl) ?: _imageUrl
}

/**
 * 帖子视频数据类
 * 表示帖子中包含的视频信息
 */
data class PostVideo(
    // 视频的唯一标识符
    val id: String,
    // 视频URL，使用私有字段配合getter进行URL转换
    @SerializedName("videoUrl") private val _videoUrl: String,
    // 视频封面URL，使用私有字段配合getter进行URL转换
    @SerializedName("coverUrl") private val _coverUrl: String?,
    // 视频时长（秒），可选字段
    val duration: Int?
) {
    /**
     * 获取处理后的视频URL
     * 自动转换localhost URL为可访问的URL，如果转换失败则返回原URL
     */
    val videoUrl: String
        get() = NetworkUtils.convertLocalhostUrl(_videoUrl) ?: _videoUrl
    
    /**
     * 获取处理后的视频封面URL
     * 自动转换localhost URL为可访问的URL
     */
    val coverUrl: String?
        get() = NetworkUtils.convertLocalhostUrl(_coverUrl)
}

/**
 * 用户数据模型类（兼容旧版本API）
 * 用于表示用户基本信息，保持与旧版本API的兼容性
 */
data class User(
    // 用户的唯一标识符
    val id: String,
    // 用户显示名称
    val name: String,
    // 用户名，使用SerializedName注解映射下划线命名的JSON字段
    @SerializedName("user_name") val userName: String?,
    // 用户头像URL，使用SerializedName注解映射下划线命名的JSON字段
    @SerializedName("user_avatar") val userAvatar: String?,
    // 用户图片，兼容旧版本API的字段
    val image: String?
)

/**
 * 评论数据模型类
 * 表示帖子的评论信息
 */
data class Comment(
    // 评论的唯一标识符
    val id: String,
    // 评论所属帖子的ID，使用SerializedName注解映射下划线命名的JSON字段
    @SerializedName("post_id") val postId: String,
    // 评论标题
    val title: String,
    // 评论内容，可选字段
    val content: String?,
    // 评论点赞数
    val likes: Int,
    // 评论作者用户信息
    val user: User,
    // 评论创建时间，使用SerializedName注解映射JSON字段
    @SerializedName("createdAt") val createdAt: String
)