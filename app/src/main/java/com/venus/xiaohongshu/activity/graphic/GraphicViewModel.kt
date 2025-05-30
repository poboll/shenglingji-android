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
    
    // 错误信息
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    // 是否使用Mock数据
    var usingMockData by mutableStateOf(false)
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
                    // 暂时不加载评论，因为后端还没有实现评论API
                    // loadComments(postId)
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
            avatar = mockUserBean.userAvatar
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
                videoUrl = mockVideo.videoUrl,
                coverUrl = mockVideo.coverUrl,
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
    }
    
    /**
     * 加载评论（暂时未使用，等待后端实现评论API）
     */
    private fun loadComments(postId: String) {
        viewModelScope.launch {
            // Comments are often loaded after post details, so isLoading might already be true
            // If you want separate loading indicators for comments, add another state variable
            try {
                Log.d(TAG, "正在加载评论，帖子ID: $postId")
                // 注意：后端目前还没有实现评论API，这里保留代码结构
                // val response = RetrofitClient.apiService.getPostComments(postId)
                // if (response.success) {
                //     comments.clear()
                //     comments.addAll(response.data)
                //     Log.d(TAG, "评论加载成功，数量: ${response.data.size}")
                // } else {
                //     val commentErrorMsg = "API获取评论失败: success=${response.success}"
                //     Log.e(TAG, commentErrorMsg)
                // }
            } catch (e: Exception) {
                Log.e(TAG, "加载评论网络请求失败: ${e.message}", e)
                // Optionally set a specific error message for comments
                // if (errorMessage == null) errorMessage = "加载评论失败: ${e.message}"
            }
        }
    }
    
    /**
     * 加载帖子详情 - 别名方法，保持向后兼容
     */
    fun loadPostDetail(postId: String) {
        loadGraphicPost(postId)
    }
}