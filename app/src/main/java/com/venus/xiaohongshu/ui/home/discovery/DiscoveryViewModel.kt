package com.venus.xiaohongshu.ui.home.discovery

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.venus.xiaohongshu.data.Post
import com.venus.xiaohongshu.data.model.Post as OldPost
import com.venus.xiaohongshu.data.model.PostType
import com.venus.xiaohongshu.network.RetrofitClient
import com.venus.xiaohongshu.ui.home.HomeDataRepository
import com.venus.xiaohongshu.ui.home.bean.GraphicCardBean
import com.venus.xiaohongshu.ui.home.bean.GraphicCardType
import com.venus.xiaohongshu.ui.home.bean.UserBean
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Description: 植物页面ViewModel
 *
 * @author: poboll
 * @date: 2024/06/03
 */
class DiscoveryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = HomeDataRepository(application.applicationContext)
    private val TAG = "DiscoveryViewModel"
    
    // 保持旧的GraphicCardBean列表用于兼容现有UI
    var graphicCardList = MutableLiveData<MutableList<GraphicCardBean>>()
    
    // 新的帖子列表
    var posts = MutableLiveData<List<Post>>()
    
    // 加载状态
    private var isLoading = false

    fun load(page: Int = 1, limit: Int = 10) {
        if (isLoading) return
        isLoading = true
        
        viewModelScope.launch {
            try {
                // 尝试使用新的API获取植物帖子数据
                Log.d(TAG, "正在从后端API获取植物帖子列表，page: $page, limit: $limit")
                val response = RetrofitClient.apiService.getPlantPosts(page, limit)
                
                if (response.success) {
                    val postsList = response.data.posts
                    posts.postValue(postsList)
                    
                    // 将新的Post转换为GraphicCardBean以兼容旧UI
                    val cards = postsList.map { post ->
                        GraphicCardBean.fromPost(post)
                    }.toMutableList()
                    
                    graphicCardList.postValue(cards)
                    Log.d(TAG, "成功从API获取数据，共 ${postsList.size} 条")
                } else {
                    Log.e(TAG, "API返回错误状态: success=${response.success}")
                    // 回退到本地数据
                    fallbackToLocalData()
                }
            } catch (e: Exception) {
                Log.e(TAG, "API请求失败: ${e.message}", e)
                // 回退到本地数据
                fallbackToLocalData()
            } finally {
                isLoading = false
            }
        }
    }

    fun reload(): Job {
        return viewModelScope.launch {
            try {
                // 尝试使用新的API刷新植物帖子数据
                Log.d(TAG, "正在刷新植物帖子列表...")
                val response = RetrofitClient.apiService.getPlantPosts(page = 1, limit = 20)
                
                if (response.success) {
                    val postsList = response.data.posts
                    posts.postValue(postsList)
                    
                    // 将新的Post转换为GraphicCardBean以兼容旧UI
                    val cards = postsList.map { post ->
                        GraphicCardBean.fromPost(post)
                    }.toMutableList()
                    
                    graphicCardList.postValue(cards)
                    Log.d(TAG, "成功刷新数据，共 ${postsList.size} 条")
                } else {
                    Log.e(TAG, "API刷新返回错误状态: success=${response.success}")
                    // 回退到本地数据
                    fallbackToLocalData()
                }
            } catch (e: Exception) {
                Log.e(TAG, "API刷新请求失败: ${e.message}", e)
                // 回退到本地数据
                fallbackToLocalData()
            }
        }
    }
    
    private suspend fun fallbackToLocalData() {
        Log.d(TAG, "回退到本地模拟数据")
        // 使用原有的repository获取本地模拟数据
        repository.getPlantFeeds().onSuccess { oldPosts ->
            val graphicCards = oldPosts.map { post ->
                GraphicCardBean.fromPost(post) 
            }.toMutableList()
            graphicCardList.postValue(graphicCards)
        }.onFailure { 
            // Handle failure to load mock data, though unlikely
            Log.e(TAG, "Failed to load mock plant feeds as fallback", it)
            graphicCardList.postValue(mutableListOf()) // Post empty list on fallback failure
        }
    }
    
    private suspend fun loadPostsFromApi(): List<GraphicCardBean> {
        return try {
            val response = RetrofitClient.apiService.getPlantPosts()
            if (response.success) {
                response.data.posts.map { post ->
                    GraphicCardBean(
                        id = post.id,
                        title = post.title,
                        imageUrl = post.getDisplayCover(),
                        likes = post.likes,
                        user = UserBean(
                            id = post.author.id,
                            name = post.author.username,
                            image = 0, // 保留兼容性
                            userAvatar = post.author.avatar
                        ),
                        type = when (post.mediaType) {
                            "video" -> GraphicCardType.Video
                            else -> GraphicCardType.Graphic
                        },
                        videoUrl = if (post.isVideoPost()) post.getMediaFileUrl() else null
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("DiscoveryViewModel", "加载植物帖子失败", e)
            emptyList()
        }
    }
}