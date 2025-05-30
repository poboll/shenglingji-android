package com.venus.xiaohongshu.api

import com.venus.xiaohongshu.data.model.Post
import com.venus.xiaohongshu.data.model.PostResponse
import com.venus.xiaohongshu.data.model.HealthCheckResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 后端API服务接口
 */
interface ApiService {
    /**
     * 获取植物帖子列表
     */
    @GET("posts/plants")
    suspend fun getPlantPosts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<PostResponse>
    
    /**
     * 获取动物帖子列表
     */
    @GET("posts/animals")
    suspend fun getAnimalPosts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<PostResponse>
    
    /**
     * 获取帖子详情
     */
    @GET("posts/{id}")
    suspend fun getPostDetail(@Path("id") postId: Int): Response<Post> // 假设帖子详情直接返回Post模型

    @GET("health")
    suspend fun healthCheck(): Response<HealthCheckResponse>

    companion object {
        private const val BASE_URL = "http://10.0.2.2:3000/api/" // 后端地址和端口

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
} 