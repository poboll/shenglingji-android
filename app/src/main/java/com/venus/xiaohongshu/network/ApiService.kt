package com.venus.xiaohongshu.network

import com.venus.xiaohongshu.data.CommentResponse
import com.venus.xiaohongshu.data.PostDetailResponse
import com.venus.xiaohongshu.data.PostsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API接口定义
 */
interface ApiService {
    
    /**
     * 获取植物帖子列表
     * @param page 页码
     * @param limit 每页数量
     */
    @GET("posts/plants")
    suspend fun getPlantPosts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): PostsResponse
    
    /**
     * 获取动物帖子列表
     * @param page 页码
     * @param limit 每页数量
     */
    @GET("posts/animals")
    suspend fun getAnimalPosts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): PostsResponse
    
    /**
     * 获取帖子详情
     * @param postId 帖子ID
     */
    @GET("posts/{postId}")
    suspend fun getPostDetail(
        @Path("postId") postId: String
    ): PostDetailResponse
}