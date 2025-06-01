package com.venus.xiaohongshu.activity.graphic

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.venus.xiaohongshu.data.Author
import com.venus.xiaohongshu.data.Comment
import com.venus.xiaohongshu.data.Post
import com.venus.xiaohongshu.mock.ImageMock
import com.venus.xiaohongshu.mock.PostImageMock
import com.venus.xiaohongshu.mock.TitleMock
import com.venus.xiaohongshu.mock.UserMock
import com.venus.xiaohongshu.mock.VideoMock
import com.venus.xiaohongshu.network.RetrofitClient
import com.venus.xiaohongshu.utils.SessionManager
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * Description:
 *
 * @Author: HuaJ1a
 * @Date: 2024/11/28
 */
class GraphicViewModel(application: Application): AndroidViewModel(application) {
    
    private val context = application.applicationContext
    private val sessionManager by lazy { SessionManager(context) }
    
    var id: String = ""
    
    // 帖子详情 - Corrected Type
    var graphicPost by mutableStateOf<Post?>(null)
        private set
    
    // 评论列表 - Corrected Type
    var comments = mutableStateListOf<Comment>()
        private set
    
    // 加载状态
    var isLoading by mutableStateOf(false)
        private set
    
    // 评论加载状态
    var isCommentsLoading by mutableStateOf(false)
        private set
    
    // 评论提交状态
    var isSubmittingComment by mutableStateOf(false)
        private set
    
    // 错误信息
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    // 评论错误信息
    var commentErrorMessage by mutableStateOf<String?>(null)
        private set
    
    // 是否使用Mock数据
    var usingMockData by mutableStateOf(false)
        private set
    
    // 点赞状态
    var isLiked by mutableStateOf(false)
        private set
    
    // 收藏状态
    var isBookmarked by mutableStateOf(false)
        private set
    
    // 评论输入框焦点状态
    var shouldFocusCommentInput by mutableStateOf(false)
        private set
    
    private val TAG = "GraphicViewModel"
    
    /**
     * 加载帖子详情
     */
    fun loadGraphicPost(postId: String) {
        if (postId.isEmpty()) {
            Log.w(TAG, "Post ID is empty, cannot load details.")
            errorMessage = "无效的帖子ID"
            isLoading = false // Ensure loading state is reset
            loadMockPostDetail(postId) // 使用模拟数据
            return
        }
        this.id = postId // Store the id if needed for retry or refresh logic
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            graphicPost = null // Clear previous post data
            comments.clear() // Clear previous comments
            usingMockData = false
            
            try {
                Log.d(TAG, "正在加载帖子详情，ID: $postId")
                val response = RetrofitClient.apiService.getPostDetail(postId)
                if (response.success) {
                    val postData = response.data
                    // 确保所有必要字段都有值
                    if (postData.coverImage == null || postData.mediaUrl == null || postData.mediaType == null) {
                        val enhancedPost = postData.copy(
                            coverImage = postData.coverImage 
                                ?: postData.videos?.firstOrNull()?.coverUrl 
                                ?: postData.images?.firstOrNull()?.imageUrl,
                            mediaUrl = postData.mediaUrl 
                                ?: if (postData.videos?.isNotEmpty() == true) postData.videos.first().videoUrl
                                else postData.images?.firstOrNull()?.imageUrl,
                            mediaType = postData.mediaType ?: if (postData.videos?.isNotEmpty() == true) "video" else "image"
                        )
                        graphicPost = enhancedPost
                        Log.d(TAG, "帖子详情数据已补充完整: $enhancedPost")
                    } else {
                        graphicPost = postData
                        Log.d(TAG, "帖子详情加载成功: $postData")
                    }
                    
                    // 如果帖子中包含评论数据，则直接使用
                    if (postData.commentsList?.isNotEmpty() == true) {
                        comments.clear()
                        comments.addAll(postData.commentsList)
                        Log.d(TAG, "帖子中包含评论数据，共 ${comments.size} 条")
                    } else {
                        // 否则加载评论
                        loadComments(postId)
                    }
                } else {
                    val errorMsg = "API获取帖子详情失败: 未知错误"
                    Log.e(TAG, errorMsg)
                    errorMessage = errorMsg
                    
                    // 失败后使用模拟数据
                    loadMockPostDetail(postId)
                }
            } catch (e: Exception) {
                val errorMsg = when (e) {
                    is HttpException -> "服务器错误(${e.code()}): ${getErrorMessageFromResponse(e)}"
                    is IOException -> "网络错误: ${e.message}"
                    else -> "加载失败: ${e.message}"
                }
                Log.e(TAG, "加载帖子详情网络请求失败: $errorMsg", e)
                errorMessage = errorMsg
                
                // 失败后使用模拟数据
                loadMockPostDetail(postId)
            } finally {
                isLoading = false
            }
        }
    }
    
    /**
     * 发表评论
     */
    fun postComment(content: String) {
        if (content.isBlank()) {
            Log.w(TAG, "评论内容为空，无法提交")
            commentErrorMessage = "评论内容不能为空"
            return
        }
        
        if (id.isEmpty()) {
            Log.w(TAG, "帖子ID为空，无法提交评论")
            commentErrorMessage = "无效的帖子ID"
            return
        }
        
        viewModelScope.launch {
            isSubmittingComment = true
            commentErrorMessage = null
            
            try {
                Log.d(TAG, "正在提交评论，帖子ID: $id")
                val response = RetrofitClient.apiService.createComment(
                    postId = id,
                    content = mapOf("content" to content)
                )
                
                if (response.success) {
                    val newComment = response.data.firstOrNull()
                    if (newComment != null) {
                        // 添加到评论列表头部
                        comments.add(0, newComment)
                        Log.d(TAG, "评论提交成功: $newComment")
                    } else {
                        Log.w(TAG, "评论提交成功但未返回评论数据")
                    }
                } else {
                    val errorMsg = "提交评论失败: 未知错误"
                    Log.e(TAG, errorMsg)
                    commentErrorMessage = errorMsg
                }
            } catch (e: Exception) {
                val errorMsg = when (e) {
                    is HttpException -> {
                        if (e.code() == 401) {
                            "请先登录后再评论"
                        } else {
                            "服务器错误(${e.code()}): ${getErrorMessageFromResponse(e)}"
                        }
                    }
                    is IOException -> "网络错误: ${e.message}"
                    else -> "提交失败: ${e.message}"
                }
                Log.e(TAG, "提交评论请求失败: $errorMsg", e)
                commentErrorMessage = errorMsg
            } finally {
                isSubmittingComment = false
            }
        }
    }
    
    /**
     * 点赞帖子
     */
    fun likePost() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "正在点赞帖子，帖子ID: $id")
                
                // 切换点赞状态
                isLiked = !isLiked
                
                // 更新帖子的点赞数
                graphicPost?.let { post ->
                    val newLikes = if (isLiked) post.likes + 1 else post.likes - 1
                    graphicPost = post.copy(likes = newLikes)
                }
                
                Log.d(TAG, "帖子点赞状态已更新: $isLiked")
                
                // 这里可以添加网络请求来同步到服务器
                // val response = RetrofitClient.apiService.likePost(id)
                
            } catch (e: Exception) {
                Log.e(TAG, "点赞帖子失败: ${e.message}", e)
                // 如果失败，回滚状态
                isLiked = !isLiked
                graphicPost?.let { post ->
                    val newLikes = if (isLiked) post.likes + 1 else post.likes - 1
                    graphicPost = post.copy(likes = newLikes)
                }
            }
        }
    }
    
    /**
     * 收藏帖子
     */
    fun bookmarkPost() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "正在收藏帖子，帖子ID: $id")
                
                // 切换收藏状态
                isBookmarked = !isBookmarked
                
                Log.d(TAG, "帖子收藏状态已更新: $isBookmarked")
                
                // 这里可以添加网络请求来同步到服务器
                // val response = RetrofitClient.apiService.bookmarkPost(id)
                
            } catch (e: Exception) {
                Log.e(TAG, "收藏帖子失败: ${e.message}", e)
                // 如果失败，回滚状态
                isBookmarked = !isBookmarked
            }
        }
    }
    
    /**
     * 触发评论输入框焦点
     */
    fun focusCommentInput() {
        shouldFocusCommentInput = true
        Log.d(TAG, "触发评论输入框焦点")
    }
    
    /**
     * 重置评论输入框焦点状态
     */
    fun resetCommentInputFocus() {
        shouldFocusCommentInput = false
    }

    /**
     * 点赞评论
     */
    fun likeComment(commentId: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "正在点赞评论，评论ID: $commentId")
                val response = RetrofitClient.apiService.likeComment(commentId)
                
                if (response.success) {
                    // 更新本地评论数据的点赞数
                    val index = comments.indexOfFirst { it.id == commentId }
                    if (index != -1) {
                        val updatedComment = comments[index].copy(likes = comments[index].likes + 1)
                        comments[index] = updatedComment
                        Log.d(TAG, "评论点赞成功: $updatedComment")
                    }
                } else {
                    Log.e(TAG, "评论点赞失败: 未知错误")
                }
            } catch (e: Exception) {
                Log.e(TAG, "评论点赞请求失败: ${e.message}", e)
            }
        }
    }
    
    /**
     * 从HttpException中提取错误信息
     */
    private fun getErrorMessageFromResponse(e: HttpException): String {
        val errorBody = e.response()?.errorBody()?.string()
        return if (errorBody?.contains("\"message\"") == true) {
            try {
                // 简单解析JSON，获取message字段
                val messageRegex = "\"message\"\\s*:\\s*\"([^\"]+)\"".toRegex()
                val matchResult = messageRegex.find(errorBody)
                matchResult?.groupValues?.get(1) ?: "未知错误"
            } catch (ex: Exception) {
                "解析错误响应失败: ${ex.message}"
            }
        } else {
            "服务器错误"
        }
    }
    
    /**
     * 加载模拟的帖子详情数据
     */
    private fun loadMockPostDetail(postId: String = "") {
        usingMockData = true
        // 创建一个模拟的帖子详情
        val mockUserBean = UserMock.provideRandomUser(context)
        val mockAuthor = Author(
            id = mockUserBean.id,
            username = mockUserBean.userName ?: "模拟用户",
            _avatar = mockUserBean.userAvatar
        )
        
        // 使用传入的postId，如果为空则生成随机ID
        val usePostId = if (postId.isEmpty()) "mock-${System.currentTimeMillis()}" else postId
        
        // 根据ID决定内容类型，实现稳定的测试数据
        val useId = usePostId.hashCode()
        val isVideoPost = useId % 3 == 0 // 约1/3概率生成视频帖子
        val postType = if (useId % 2 == 0) 1 else 2 // 植物或动物类型
        
        val videoData = if (isVideoPost) {
            // 将VideoMock返回的模型转换为数据模型所需的类型
            val mockVideo = VideoMock.provideRandomVideo(context)
            listOf(com.venus.xiaohongshu.data.PostVideo(
                id = mockVideo.id,
                _videoUrl = mockVideo.videoUrl,
                _coverUrl = mockVideo.coverUrl,
                duration = mockVideo.duration
            ))
        } else null
        
        val imageData = if (!isVideoPost) {
            val imageCount = (1..5).random()
            PostImageMock.provideRandomImages(context, imageCount)
        } else null
        
        val mockPost = Post(
            id = usePostId,
            title = TitleMock.getRandomTitle(),
            content = "这是一个模拟的帖子内容，用于测试展示。当网络请求失败时，我们会显示这个模拟数据。",
            type = postType, // 随机植物或动物类型
            mediaType = if (isVideoPost) "video" else "image",
            location = "模拟位置",
            likes = (10..500).random(),
            views = (100..1000).random(),
            comments = (0..50).random(),
            author = mockAuthor,
            images = imageData,
            videos = videoData,
            coverImage = videoData?.firstOrNull()?.coverUrl ?: imageData?.firstOrNull()?.imageUrl,
            mediaUrl = if (isVideoPost) videoData?.firstOrNull()?.videoUrl else imageData?.firstOrNull()?.imageUrl,
            createdAt = "2024-05-30T08:00:00Z",
            updatedAt = "2024-05-30T08:00:00Z",
            isHot = Math.random() > 0.7
        )
        
        graphicPost = mockPost
        Log.d(TAG, "已加载模拟帖子数据: $mockPost")
        
        // 生成模拟评论
        loadMockComments()
    }
    
    /**
     * 加载帖子评论
     */
    private fun loadComments(postId: String) {
        viewModelScope.launch {
            isCommentsLoading = true
            commentErrorMessage = null
            
            try {
                Log.d(TAG, "正在加载评论，帖子ID: $postId")
                val response = RetrofitClient.apiService.getPostComments(postId)
                if (response.success) {
                    comments.clear()
                    comments.addAll(response.data)
                    Log.d(TAG, "评论加载成功，数量: ${response.data.size}")
                } else {
                    val commentErrorMsg = "API获取评论失败: success=${response.success}"
                    Log.e(TAG, commentErrorMsg)
                    commentErrorMessage = commentErrorMsg
                    
                    // 如果加载真实评论失败，但使用了真实帖子数据，则尝试生成模拟评论
                    if (!usingMockData) {
                        loadMockComments()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "加载评论网络请求失败: ${e.message}", e)
                commentErrorMessage = "加载评论失败: ${e.message}"
                
                // 如果加载真实评论失败，但使用了真实帖子数据，则尝试生成模拟评论
                if (!usingMockData) {
                    loadMockComments()
                }
            } finally {
                isCommentsLoading = false
            }
        }
    }
    
    /**
     * 加载模拟评论数据
     */
    private fun loadMockComments() {
        // 生成5-15条随机评论
        val commentCount = (5..15).random()
        val mockComments = List(commentCount) { index ->
            val mockUserBean = UserMock.provideRandomUser(context)
            val createdTime = System.currentTimeMillis() - (1000 * 60 * 60 * (1..72).random())
            
            Comment(
                id = "mock-comment-$index",
                postId = id,
                title = "",
                content = "这是第${index + 1}条模拟评论，用于测试UI显示。" + 
                    if (index % 3 == 0) "这是一条稍长的评论内容，可以测试多行文本的显示效果。" else "",
                likes = (0..50).random(),
                user = com.venus.xiaohongshu.data.User(
                    id = mockUserBean.id,
                    name = mockUserBean.name,
                    userName = mockUserBean.userName ?: "用户${index + 1}",
                    userAvatar = mockUserBean.userAvatar,
                    image = mockUserBean.image.toString()
                ),
                createdAt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault()).apply {
                    timeZone = java.util.TimeZone.getTimeZone("UTC")
                }.format(java.util.Date(createdTime))
            )
        }
        
        comments.clear()
        comments.addAll(mockComments)
        Log.d(TAG, "已加载${mockComments.size}条模拟评论")
    }
    
    /**
     * 加载帖子详情 - 别名方法，保持向后兼容
     */
    fun loadPostDetail(postId: String) {
        loadGraphicPost(postId)
    }
}