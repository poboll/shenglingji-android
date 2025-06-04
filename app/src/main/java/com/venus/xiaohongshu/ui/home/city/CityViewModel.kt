package com.venus.xiaohongshu.ui.home.city

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
 * Description: 动物页面ViewModel
 *
 * @author: poboll
 * @date: 2024/05/25
 */
class CityViewModel(application: Application): AndroidViewModel(application) {
    private val repository = HomeDataRepository(application.applicationContext)
    private val TAG = "CityViewModel"
    
    // 保持旧的GraphicCardBean列表用于兼容现有UI
    var cityGraphicCardList = MutableLiveData<MutableList<GraphicCardBean>>()
    
    // 新的动物帖子列表
    var animalPosts = MutableLiveData<List<Post>>()
    
    // 加载状态
    private var isLoading = false

    fun load(page: Int = 1, limit: Int = 10) {
        if (isLoading) return
        isLoading = true
        
        viewModelScope.launch {
            try {
                // 统一API调用方式：直接使用RetrofitClient.apiService.getAnimalPosts()
                Log.d(TAG, "正在从后端API获取动物帖子列表，page: $page, limit: $limit")
                val response = RetrofitClient.apiService.getAnimalPosts(page, limit)
                
                if (response.success) {
                    val postsList = response.data.posts
                    animalPosts.postValue(postsList)
                    
                    // 将新的Post转换为GraphicCardBean以兼容旧UI
                    val cards = postsList.map { post ->
                        Log.d(TAG, "转换帖子: ${post.title}, 图片URL: ${post.getDisplayCover()}")
                        GraphicCardBean.fromPost(post)
                    }.toMutableList()
                    
                    cityGraphicCardList.postValue(cards)
                    Log.d(TAG, "成功从API获取数据，共 ${postsList.size} 条")
                } else {
                    Log.e(TAG, "API返回错误状态: success=${response.success}")
                    // 回退到repository方法
                    fallbackToRepository()
                }
            } catch (e: Exception) {
                Log.e(TAG, "API请求失败: ${e.message}", e)
                // 回退到repository方法
                fallbackToRepository()
            } finally {
                isLoading = false
            }
        }
    }

    fun reload(): Job {
        return viewModelScope.launch {
            try {
                // 统一API调用方式：直接使用RetrofitClient.apiService.getAnimalPosts()
                Log.d(TAG, "正在刷新动物帖子列表...")
                val response = RetrofitClient.apiService.getAnimalPosts(page = 1, limit = 20)
                
                if (response.success) {
                    val postsList = response.data.posts
                    animalPosts.postValue(postsList)
                    
                    // 将新的Post转换为GraphicCardBean以兼容旧UI
                    val cards = postsList.map { post ->
                        Log.d(TAG, "刷新转换帖子: ${post.title}, 图片URL: ${post.getDisplayCover()}")
                        GraphicCardBean.fromPost(post)
                    }.toMutableList()
                    
                    cityGraphicCardList.postValue(cards)
                    Log.d(TAG, "成功刷新数据，共 ${postsList.size} 条")
                } else {
                    Log.e(TAG, "API刷新返回错误状态: success=${response.success}")
                    // 回退到repository方法
                    fallbackToRepository()
                }
            } catch (e: Exception) {
                Log.e(TAG, "API刷新请求失败: ${e.message}", e)
                // 回退到repository方法
                fallbackToRepository()
            }
        }
    }

    private suspend fun fallbackToRepository() {
        Log.d(TAG, "回退到repository方法获取动物数据")
        // 使用原有的repository获取动物数据作为备用方案
        repository.getAnimalFeeds().onSuccess { oldPosts ->
            val graphicCards = oldPosts.map { post ->
                Log.d(TAG, "Repository转换帖子: ${post.title}")
                GraphicCardBean.fromPost(post) 
            }.toMutableList()
            cityGraphicCardList.postValue(graphicCards)
            Log.d(TAG, "Repository备用方案成功，共 ${oldPosts.size} 条")
        }.onFailure { 
            Log.e(TAG, "Repository备用方案也失败", it)
            cityGraphicCardList.postValue(mutableListOf()) // 最终失败时返回空列表
        }
    }
    
    private suspend fun loadAnimalPostsFromApi(): List<GraphicCardBean> {
        return try {
            val response = RetrofitClient.apiService.getAnimalPosts()
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
                            image = 0,
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
            Log.e(TAG, "加载动物帖子失败", e)
            emptyList()
        }
    }
}