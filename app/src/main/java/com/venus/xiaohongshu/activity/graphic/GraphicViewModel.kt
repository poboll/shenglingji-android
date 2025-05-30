package com.venus.xiaohongshu.activity.graphic

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.venus.xiaohongshu.data.Comment
import com.venus.xiaohongshu.data.Post
import com.venus.xiaohongshu.network.RetrofitClient
import kotlinx.coroutines.launch

/**
 * Description:
 *
 * @Author: HuaJ1a
 * @Date: 2024/11/28
 */
class GraphicViewModel(application: Application): AndroidViewModel(application) {
    
    // private val repository = HomeDataRepository(application.applicationContext)
    
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
    
    private val TAG = "GraphicViewModel"
    
    /**
     * 加载帖子详情
     */
    fun loadPostDetail(postId: String) {
        if (postId.isEmpty()) {
            Log.w(TAG, "Post ID is empty, cannot load details.")
            errorMessage = "无效的帖子ID"
            isLoading = false // Ensure loading state is reset
            return
        }
        this.id = postId // Store the id if needed for retry or refresh logic
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            graphicPost = null // Clear previous post data
            comments.clear() // Clear previous comments
            
            try {
                Log.d(TAG, "正在加载帖子详情，ID: $postId")
                val response = RetrofitClient.apiService.getPostDetail(postId)
                if (response.success) {
                    graphicPost = response.data
                    Log.d(TAG, "帖子详情加载成功: ${response.data}")
                    // 暂时不加载评论，因为后端还没有实现评论API
                    // loadComments(postId)
                } else {
                    val errorMsg = "API获取帖子详情失败: success=${response.success}, Data: ${response.data}"
                    Log.e(TAG, errorMsg)
                    errorMessage = errorMsg
                }
            } catch (e: Exception) {
                Log.e(TAG, "加载帖子详情网络请求失败: ${e.message}", e)
                errorMessage = "加载失败: ${e.message}"
            } finally {
                isLoading = false
            }
        }
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
}