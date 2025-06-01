package com.venus.xiaohongshu.network

import com.venus.xiaohongshu.data.CommentResponse
import com.venus.xiaohongshu.data.CommentListResponse
import com.venus.xiaohongshu.data.PostDetailResponse
import com.venus.xiaohongshu.data.PostsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API接口定义
 */
interface ApiService {
    
    /**
     * 搜索帖子
     * @param query 搜索关键词
     * @param type 帖子类型：1=植物，2=动物，不传则搜索全部
     * @param page 页码
     * @param limit 每页数量
     * @param sort 排序方式：relevance=相关性, newest=最新, oldest=最早, popular=热门
     * @param mediaType 媒体类型：image=图片, video=视频, 不传则全部
     */
    @GET("posts/search")
    suspend fun searchPosts(
        @Query("query") query: String,
        @Query("type") type: Int? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("sort") sort: String = "relevance",
        @Query("mediaType") mediaType: String? = null
    ): PostsResponse
    
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
    
    /**
     * 获取帖子的评论
     * @param postId 帖子ID
     */
    @GET("comments/post/{postId}")
    suspend fun getPostComments(
        @Path("postId") postId: String
    ): CommentListResponse
    
    /**
     * 发表评论
     * @param postId 帖子ID
     * @param content 评论内容
     */
    @POST("comments/post/{postId}")
    suspend fun createComment(
        @Path("postId") postId: String,
        @Body content: Map<String, String>
    ): CommentResponse
    
    /**
     * 点赞评论
     * @param commentId 评论ID
     */
    @POST("comments/like/{commentId}")
    suspend fun likeComment(
        @Path("commentId") commentId: String
    ): CommentResponse
}