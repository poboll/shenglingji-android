package com.venus.xiaohongshu.activity.video

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.venus.xiaohongshu.R
import com.venus.xiaohongshu.api.ApiService
import com.venus.xiaohongshu.ui.home.HomeDataRepository
import com.venus.xiaohongshu.ui.home.bean.GraphicCardBean
import com.venus.xiaohongshu.ui.home.bean.GraphicCardType
import com.venus.xiaohongshu.ui.home.bean.UserBean
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.UUID
import kotlin.random.Random

/**
 * Description:
 *
 * @author: poboll
 * @date: 2024/06/02
 */
class VideoViewModel(application: Application): AndroidViewModel(application) {

    private val repository = HomeDataRepository(application.applicationContext)
    private val apiService = ApiService.create()
    private val TAG = "VideoViewModel"
    private val context = application.applicationContext
    
    var id: String = ""
    var graphicCardBean: GraphicCardBean? = null
    
    // 使用Compose状态管理
    var isLoading by mutableStateOf(true)
        private set
    
    var videoList by mutableStateOf<List<GraphicCardBean>>(emptyList())
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    // 点赞和收藏状态
    var isLiked by mutableStateOf(false)
        private set
    
    var isBookmarked by mutableStateOf(false)
        private set
    
    // 评论输入框焦点控制状态
    var shouldFocusCommentInput by mutableStateOf(false)
        private set
    
    // 点赞和收藏功能
    fun likePost() {
        isLiked = !isLiked
        Log.d(TAG, "点赞状态变化: $isLiked")
    }
    
    fun bookmarkPost() {
        isBookmarked = !isBookmarked
        Log.d(TAG, "收藏状态变化: $isBookmarked")
    }
    
    // 评论输入框焦点控制功能
    fun focusCommentInput() {
        shouldFocusCommentInput = true
        Log.d(TAG, "触发评论输入框焦点")
    }
    
    fun resetCommentInputFocus() {
        shouldFocusCommentInput = false
        Log.d(TAG, "重置评论输入框焦点状态")
    }

    // 可用的本地视频资源列表
    private val localVideos = listOf(
        R.raw.video_1,
        R.raw.video_2,
        R.raw.video_3
    )
    
    fun init() {
        if (id.isEmpty()) {
            Log.e(TAG, "初始化失败: id为空")
            isLoading = false
            errorMessage = "视频ID为空"
            return
        }
        
        isLoading = true
        errorMessage = null
        
        viewModelScope.launch {
            try {
                // 尝试通过API获取帖子详情
                val isBackendId = id.startsWith("post-")
                
                if (isBackendId) {
                    // 后端格式ID（如"post-plant-1"）
                    Log.d(TAG, "检测到后端格式ID: $id，尝试从API获取")
                    try {
                        // 提取数字部分作为API请求ID
                        val postIdNumber = id.substringAfterLast("-").toIntOrNull()
                        if (postIdNumber != null) {
                            val response = apiService.getPostDetail(id)
                            if (response.isSuccessful && response.body() != null) {
                                val apiResponse = response.body()!!
                                if (apiResponse.success && apiResponse.data != null) {
                                    val post = apiResponse.data
                                    // 转换为GraphicCardBean
                                    graphicCardBean = GraphicCardBean.fromPost(post)
                                    Log.d(TAG, "成功从API获取帖子: ${post.title}")
                                    
                                    // 检查是否有视频数据
                                    if (post.videos != null && post.videos.isNotEmpty()) {
                                        Log.d(TAG, "帖子包含视频: ${post.videos.size} 个, 第一个视频URL: ${post.videos[0].videoUrl}")
                                    } else {
                                        Log.w(TAG, "帖子没有视频数据")
                                    }
                                } else {
                                    Log.e(TAG, "API返回的数据格式不正确")
                                }
                            } else {
                                Log.e(TAG, "API获取帖子失败: ${response.code()} - ${response.message()}")
                            }
                        } else {
                            Log.e(TAG, "无法从ID提取数字部分: $id")
                        }
                    } catch (e: HttpException) {
                        Log.e(TAG, "API请求错误: ${e.message()}")
                    } catch (e: Exception) {
                        Log.e(TAG, "API请求异常: ${e.message}")
                    }
                }
                
                // 无论API请求是否成功，都获取本地数据
                val graphicCardList = repository.getGraphicCardList()
                Log.d(TAG, "获取到本地帖子列表，总数: ${graphicCardList.size}")
                
                // 如果还没有通过API获取到帖子，尝试从本地列表中查找
                if (graphicCardBean == null) {
                    graphicCardBean = graphicCardList.find { it.id == id }
                    if (graphicCardBean == null) {
                        Log.w(TAG, "未找到ID为 $id 的帖子，将使用视频列表")
                    }
                }
                
                // 筛选视频帖子并确保有有效视频资源
                val filteredVideoList = graphicCardList.filter { card ->
                    card.type == GraphicCardType.Video
                }.map { ensureVideoResource(it) }
                
                Log.d(TAG, "筛选出视频帖子数量: ${filteredVideoList.size}")
                
                val newVideoList = mutableListOf<GraphicCardBean>()
                
                // 如果有当前帖子且是视频类型，确保它排在第一位
                graphicCardBean?.let {
                    if (it.type == GraphicCardType.Video) {
                        // 确保有视频资源
                        val validCard = ensureVideoResource(it)
                        newVideoList.add(validCard)
                        Log.d(TAG, "添加当前帖子到视频列表: ${validCard.title}, videoUrl=${validCard.videoUrl}, video=${validCard.video}")
                    } else {
                        Log.e(TAG, "当前帖子不是视频类型: ${it.type}")
                    }
                }
                
                // 添加其他视频帖子，避免重复
                newVideoList.addAll(filteredVideoList.filter { it.id != id })
                
                // 如果没有任何视频，添加本地测试视频
                if (newVideoList.isEmpty()) {
                    Log.e(TAG, "没有可用的视频帖子，添加本地测试视频")
                    addLocalTestVideos(newVideoList, 3) // 添加3个测试视频
                } else {
                    Log.d(TAG, "视频列表加载完成，共 ${newVideoList.size} 个视频")
                    // 打印第一个视频信息用于调试
                    if (newVideoList.isNotEmpty()) {
                        val firstVideo = newVideoList[0]
                        Log.d(TAG, "第一个视频信息: id=${firstVideo.id}, title=${firstVideo.title}, " +
                               "videoUrl=${firstVideo.videoUrl}, video=${firstVideo.video}")
                    }
                }
                
                // 更新状态
                videoList = newVideoList
                isLoading = false
                errorMessage = null
            } catch (e: Exception) {
                Log.e(TAG, "初始化过程中出错: ${e.message}", e)
                // 出错时添加本地测试视频
                val testVideoList = mutableListOf<GraphicCardBean>()
                addLocalTestVideos(testVideoList, 3)
                videoList = testVideoList
                isLoading = false
                errorMessage = "加载失败: ${e.message}"
            }
        }
    }
    
    // 确保视频卡片有可用的视频资源
    private fun ensureVideoResource(card: GraphicCardBean): GraphicCardBean {
        // 如果有视频URL并且不是空的，保留URL
        if (!card.videoUrl.isNullOrEmpty()) {
            // 检查是否是http://localhost或10.0.2.2开头的URL
            val fixedUrl = when {
                card.videoUrl.startsWith("http://localhost") -> {
                    // 替换localhost为10.0.2.2(Android模拟器访问主机的特殊IP)
                    card.videoUrl.replace("http://localhost", "http://10.0.2.2")
                }
                else -> card.videoUrl
            }
            
            // 如果URL变化了，返回新的对象，保持video=0让播放器优先使用网络视频
            if (fixedUrl != card.videoUrl) {
                return card.copy(videoUrl = fixedUrl, video = 0)
            }
            
            // 有网络视频URL时，优先使用网络视频，设置video=0
            return card.copy(video = 0)
        }
        
        // 检查是否有视频资源ID
        if (card.video != 0 && localVideos.contains(card.video)) {
            // 有有效的视频资源ID，保持不变
            return card
        }
        
        // 既没有URL也没有有效的资源ID，使用默认资源
        return card.copy(
            video = getRandomLocalVideo(),
            videoUrl = null // 清空URL，确保使用本地资源
        )
    }
    
    // 添加多个本地测试视频
    private fun addLocalTestVideos(targetList: MutableList<GraphicCardBean>, count: Int) {
        val titles = listOf(
            "美丽风景记录片", 
            "动物世界纪实", 
            "城市风光展示", 
            "美食制作教程", 
            "旅行探险记录"
        )
        
        repeat(count) { index ->
            val testVideo = GraphicCardBean(
                id = UUID.randomUUID().toString(),
                title = titles[index % titles.size],
                video = localVideos[index % localVideos.size],
                videoUrl = null,
                user = UserBean(
                    id = "test-$index",
                    name = "测试用户${index + 1}",
                    image = R.drawable.icon_logo
                ),
                likes = Random.nextInt(100, 999),
                type = GraphicCardType.Video
            )
            
            targetList.add(testVideo)
            Log.d(TAG, "添加本地测试视频: title=${testVideo.title}, video=${testVideo.video}")
        }
    }
    
    // 获取随机本地视频资源
    private fun getRandomLocalVideo(): Int {
        return localVideos[Random.nextInt(localVideos.size)]
    }
}