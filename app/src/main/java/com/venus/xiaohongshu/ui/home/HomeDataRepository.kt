package com.venus.xiaohongshu.ui.home

import android.content.Context
import android.util.Log
import com.venus.xiaohongshu.api.ApiService
import com.venus.xiaohongshu.data.model.Post
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
 * @author: venus
 * @date: 2024/11/15
 */
class HomeDataRepository(private val context: Context) {
    
    // 发现图文卡
    private val graphicCardList = mutableListOf<GraphicCardBean>()
    // 同城图文卡
    private val cityGraphicCardList = mutableListOf<GraphicCardBean>()
    
    private val apiService = ApiService.create()

    suspend fun getPlantFeeds(page: Int = 1, limit: Int = 10): Result<List<Post>> {
        return getFeeds(page, limit, PostType.PLANT)
    }
    
    suspend fun getAnimalFeeds(page: Int = 1, limit: Int = 10): Result<List<Post>> {
        return getFeeds(page, limit, PostType.ANIMAL)
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
                val graphicCardBean = GraphicCardBean(
                    id = UUID.randomUUID().toString(),
                    title = TitleMock.getRandomTitle(),
                    user = user,
                    image = ImageMock.getRandomImage(),
                    video = video,
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

    private suspend fun getFeeds(page: Int, limit: Int, type: PostType): Result<List<Post>> {
        return withContext(Dispatchers.IO) {
            if (isBackendAvailable()) {
                try {
                    val response = when (type) {
                        PostType.PLANT -> apiService.getPlantPosts(page, limit)
                        PostType.ANIMAL -> apiService.getAnimalPosts(page, limit)
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

    private fun getMockFeeds(type: PostType): List<Post> {
        val mockPosts = mutableListOf<Post>()
        val titles = when (type) {
            PostType.PLANT -> TitleMock.providePlantTitles()
            PostType.ANIMAL -> TitleMock.provideAnimalTitles()
        }
        val imageProvider = when (type) {
            PostType.PLANT -> ImageMock::providePlantImages
            PostType.ANIMAL -> ImageMock::provideAnimalImages
        }

        titles.forEachIndexed { index, title ->
            val userBean = UserMock.provideRandomUser(context)
            val images = imageProvider(context, (1..5).random())
            val videos = if (Math.random() < 0.3) listOf(VideoMock.provideRandomVideo(context)) else emptyList()

            // Map mock data to the Post model structure
            val post = Post(
                id = index, // Mock ID
                title = title,
                content = "这是来自Mock数据的${type.name.lowercase()}帖子内容: $title",
                type = if (type == PostType.PLANT) 1 else 2,
                likes = (10..500).random(),
                comments = (0..100).random(),
                views = (100..1000).random(),
                isHot = Math.random() > 0.7,
                location = "模拟地点",
                createdAt = "2023-01-01T12:00:00Z", // Mock date
                updatedAt = "2023-01-01T12:00:00Z", // Mock date
                userId = userBean.id.toIntOrNull() ?: index, // Mock user ID
                author = com.venus.xiaohongshu.data.model.User( // map to new User model
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
                        id = imgIndex,
                        imageUrl = url,
                        position = imgIndex,
                        description = null
                    )
                },
                videos = videos.map { video ->
                    com.venus.xiaohongshu.data.model.PostVideo(
                        id = 0, // Mock video ID
                        videoUrl = video.videoUrl,
                        coverUrl = video.coverUrl,
                        duration = video.duration
                    )
                },
                // Mock-specific fields for compatibility if UI still uses them directly
                cover = videos.firstOrNull()?.coverUrl ?: images.firstOrNull(),
                avatar = userBean.userAvatar,
                authorName = userBean.userName,
                description = "这是来自Mock数据的${type.name.lowercase()}帖子内容: $title",
                likeCount = (10..500).random(),
                isLiked = Math.random() > 0.5
            )
            mockPosts.add(post)
        }
        return mockPosts
    }

    enum class PostType {
        PLANT, ANIMAL
    }
}