package com.venus.xiaohongshu.ui.home

import android.content.Context
import android.util.Log
import com.venus.xiaohongshu.api.ApiService
import com.venus.xiaohongshu.data.model.Post
import com.venus.xiaohongshu.data.model.PostType
import com.venus.xiaohongshu.mock.ImageMock
import com.venus.xiaohongshu.mock.TitleMock
import com.venus.xiaohongshu.mock.UserMock
import com.venus.xiaohongshu.mock.VideoMock
import com.venus.xiaohongshu.ui.home.bean.GraphicCardBean
import com.venus.xiaohongshu.ui.home.bean.GraphicCardType
import com.venus.xiaohongshu.ui.home.bean.UserBean
import kotlinx.coroutines.delay
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.random.Random

/**
 * Description: home 数据仓库，代替网络请求
 *
 * @author: poboll
 * @date: 2024/06/04
 */
class HomeDataRepository(private val context: Context) {
    
    // 发现图文卡
    private val graphicCardList = mutableListOf<GraphicCardBean>()
    // 同城图文卡
    private val cityGraphicCardList = mutableListOf<GraphicCardBean>()
    
    private val apiService = ApiService.create()

    suspend fun getPlantFeeds(page: Int = 1, limit: Int = 10): Result<List<Post>> {
        return getFeeds(page, limit, PostCategoryType.PLANT)
    }
    
    suspend fun getAnimalFeeds(page: Int = 1, limit: Int = 10): Result<List<Post>> {
        return getFeeds(page, limit, PostCategoryType.ANIMAL)
    }

    suspend fun getGraphicCardList(reload: Boolean = false): MutableList<GraphicCardBean> {
        if (graphicCardList.isEmpty() || reload) {
            val tempList = mutableListOf<GraphicCardBean>()
            repeat(200) {
                val user = UserBean(
                    id = UUID.randomUUID().toString(),
                    name = UserMock.getRandomName(),
                    image = UserMock.getRandomImage()
                )
                val type = if(Random.nextDouble() < 0.2) GraphicCardType.Video else GraphicCardType.Graphic
                val video = if (type == GraphicCardType.Video) VideoMock.getRandomVideo() else 0
                val videoUrl = if (type == GraphicCardType.Video) {
                    "android.resource://${context.packageName}/${video}"
                } else null
                val image = ImageMock.getRandomImage()
                val graphicCardBean = GraphicCardBean(
                    id = UUID.randomUUID().toString(),
                    title = TitleMock.getRandomTitle(),
                    user = user,
                    image = image,
                    imageUrl = "android.resource://${context.packageName}/${image}",
                    video = video,
                    videoUrl = videoUrl,
                    likes = Random.nextInt(999),
                    type = type
                )
                tempList.add(graphicCardBean)
                delay(3)
            }
            graphicCardList.clear()
            graphicCardList.addAll(tempList)
        }
        return graphicCardList
    }

    suspend fun getCityGraphicCardList(reload: Boolean = false): MutableList<GraphicCardBean> {
        if (cityGraphicCardList.isEmpty() || reload) {
            val tempList = mutableListOf<GraphicCardBean>()
            repeat(200) {
                val user = UserBean(
                    id = UUID.randomUUID().toString(),
                    name = UserMock.getRandomName(),
                    image = UserMock.getRandomImage()
                )
                val graphicCardBean = GraphicCardBean(
                    id = UUID.randomUUID().toString(),
                    title = TitleMock.getRandomTitle(),
                    user = user,
                    image = ImageMock.getRandomImage(),
                    likes = Random.nextInt(999)
                )
                tempList.add(graphicCardBean)
                delay(5)
            }
            cityGraphicCardList.clear()
            cityGraphicCardList.addAll(tempList)
        }
        return cityGraphicCardList
    }

    suspend fun getRecommendUserList(): MutableList<UserBean> {
        val recommendUserList = mutableListOf<UserBean>()
        repeat(10) {
            val user = UserBean(
                id = UUID.randomUUID().toString(),
                name = UserMock.getRandomName(),
                image = UserMock.getRandomImage(),
                userInfo = UserMock.gerRandomUserInfo()
            )
            recommendUserList.add(user)
            delay(50)
        }
        return recommendUserList
    }
    
    private suspend fun isBackendAvailable(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.healthCheck()
                response.isSuccessful && response.body()?.status == "ok"
            } catch (e: Exception) {
                Log.e("HomeDataRepository", "Health check failed: ${e.message}")
                false
            }
        }
    }

    private suspend fun getFeeds(page: Int, limit: Int, type: PostCategoryType): Result<List<Post>> {
        return withContext(Dispatchers.IO) {
            if (isBackendAvailable()) {
                try {
                    val response = when (type) {
                        PostCategoryType.PLANT -> apiService.getPlantPosts(page, limit)
                        PostCategoryType.ANIMAL -> apiService.getAnimalPosts(page, limit)
                    }
                    if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                        Result.success(response.body()!!.data.posts)
                    } else {
                        Log.e("HomeDataRepository", "API call failed or returned error: ${response.code()} - ${response.message()}")
                        Result.success(getMockFeeds(type)) // Fallback to mock on API error
                    }
                } catch (e: IOException) {
                    Log.e("HomeDataRepository", "API call failed with IOException: ${e.message}")
                    Result.success(getMockFeeds(type)) // Fallback to mock on network error
                } catch (e: Exception) {
                    Log.e("HomeDataRepository", "API call failed with Exception: ${e.message}")
                    Result.failure(e) // For other exceptions, propagate failure
                }
            } else {
                Log.d("HomeDataRepository", "Backend not available, using mock data for ${type.name}")
                Result.success(getMockFeeds(type))
            }
        }
    }

    private fun getMockFeeds(type: PostCategoryType): List<Post> {
        val mockPosts = mutableListOf<Post>()
        val titles = when (type) {
            PostCategoryType.PLANT -> TitleMock.providePlantTitles()
            PostCategoryType.ANIMAL -> TitleMock.provideAnimalTitles()
        }
        val imageProvider = when (type) {
            PostCategoryType.PLANT -> ImageMock::providePlantImages
            PostCategoryType.ANIMAL -> ImageMock::provideAnimalImages
        }

        titles.forEachIndexed { index, title ->
            val userBean = UserMock.provideRandomUser(context)
            val images = imageProvider(context, (1..5).random())
            val videos = if (Math.random() < 0.3) listOf(VideoMock.provideRandomVideo(context)) else emptyList()
            
            // 确定内容类型
            val contentType = if (videos.isNotEmpty()) PostType.VIDEO else PostType.IMAGE
            
            // 选择一个封面图片
            val coverImageUrl = videos.firstOrNull()?.coverUrl ?: images.firstOrNull()

            // Map mock data to the Post model structure
            val post = Post(
                id = index.toString(), // 转换为String类型
                title = title,
                content = "这是来自Mock数据的${type.name.lowercase()}帖子内容: $title",
                type = contentType, // 使用PostType枚举
                category = type.name.lowercase(), // 添加类别
                coverImage = coverImageUrl, // 封面图片
                mediaUrl = if (contentType == PostType.VIDEO) videos.firstOrNull()?.videoUrl else images.firstOrNull(), // 媒体URL
                likes = (10..500).random(),
                views = (100..1000).random(),
                author = com.venus.xiaohongshu.data.model.User(
                    id = userBean.id.toIntOrNull() ?: index,
                    username = userBean.userName ?: "Mock User",
                    avatar = userBean.userAvatar,
                    email = "${userBean.userName?.replace(" ", "")?.lowercase()}@example.com",
                    nickname = userBean.userName,
                    bio = "A mock user bio.",
                    phone = null,
                    gender = null,
                    birthday = null,
                    status = "active",
                    createdAt = "2023-01-01T12:00:00Z"
                ),
                images = images.mapIndexed { imgIndex, url ->
                    com.venus.xiaohongshu.data.model.PostImage(
                        id = imgIndex.toString(),
                        imageUrl = url,
                        position = imgIndex,
                        description = null
                    )
                },
                videos = videos.map { video ->
                    com.venus.xiaohongshu.data.model.PostVideo(
                        id = "0", // 转换为String类型
                        videoUrl = video.videoUrl,
                        coverUrl = video.coverUrl,
                        duration = video.duration
                    )
                },
                createdAt = "2023-01-01T12:00:00Z",
                updatedAt = "2023-01-01T12:00:00Z"
            )
            mockPosts.add(post)
        }
        return mockPosts
    }

    enum class PostCategoryType {
        PLANT, ANIMAL
    }
}