package com.venus.xiaohongshu.ui.home.city

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.venus.xiaohongshu.data.model.Post
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
 * @author: venus
 * @date: 2024/11/15
 */
class CityViewModel(application: Application): AndroidViewModel(application) {
    private val repository = HomeDataRepository(application.applicationContext)
    
    // 保持旧的GraphicCardBean列表用于兼容现有UI
    var cityGraphicCardList = MutableLiveData<MutableList<GraphicCardBean>>()
    
    // 新的动物帖子列表
    var animalPosts = MutableLiveData<List<Post>>()

    fun load(page: Int = 1, limit: Int = 10) {
        viewModelScope.launch {
            repository.getAnimalFeeds(page, limit).onSuccess { posts ->
                animalPosts.postValue(posts)
                val graphicCards = posts.map { post ->
                    GraphicCardBean.fromPost(post)
                }.toMutableList()
                cityGraphicCardList.postValue(graphicCards)
                Log.d("CityViewModel", "Loaded animal feeds: ${posts.size} items. First item: ${posts.firstOrNull()?.title}")
            }.onFailure {
                Log.e("CityViewModel", "Failed to load animal feeds", it)
                // graphicCardList.postValue(mutableListOf()) // 可选的错误处理
            }
        }
    }

    fun reload(): Job {
        return viewModelScope.launch {
            repository.getAnimalFeeds(page = 1, limit = 20).onSuccess { posts -> // 假设reload总是加载前20条
                animalPosts.postValue(posts)
                val graphicCards = posts.map { post ->
                    GraphicCardBean.fromPost(post)
                }.toMutableList()
                cityGraphicCardList.postValue(graphicCards)
                 Log.d("CityViewModel", "Reloaded animal feeds: ${posts.size} items. First item: ${posts.firstOrNull()?.title}")
            }.onFailure {
                Log.e("CityViewModel", "Failed to reload animal feeds", it)
                // cityGraphicCardList.postValue(mutableListOf()) // 可选的错误处理
            }
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
            Log.e("CityViewModel", "加载动物帖子失败", e)
            emptyList()
        }
    }
}