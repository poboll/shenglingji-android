// 定义包名，标识API服务相关类的命名空间
package com.venus.xiaohongshu.api

// 导入帖子数据模型
import com.venus.xiaohongshu.data.model.Post
// 导入帖子响应数据模型
import com.venus.xiaohongshu.data.model.PostResponse
// 导入健康检查响应数据模型
import com.venus.xiaohongshu.data.model.HealthCheckResponse
// 导入Retrofit的Response包装类
import retrofit2.Response
// 导入Retrofit核心类，用于构建HTTP客户端
import retrofit2.Retrofit
// 导入Gson转换器工厂，用于JSON序列化和反序列化
import retrofit2.converter.gson.GsonConverterFactory
// 导入GET注解，用于标记HTTP GET请求
import retrofit2.http.GET
// 导入Path注解，用于URL路径参数
import retrofit2.http.Path
// 导入Query注解，用于URL查询参数
import retrofit2.http.Query

/**
 * 后端API服务接口
 * 定义了与后端服务器通信的所有API端点
 * 使用Retrofit框架进行网络请求
 */
interface ApiService {
    /**
     * 获取植物类型帖子列表
     * @param page 页码，默认为第1页
     * @param limit 每页数量限制，默认为10条
     * @return 包含植物帖子列表的响应对象
     */
    @GET("posts/plants")
    suspend fun getPlantPosts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<PostResponse>
    
    /**
     * 获取动物类型帖子列表
     * @param page 页码，默认为第1页
     * @param limit 每页数量限制，默认为10条
     * @return 包含动物帖子列表的响应对象
     */
    @GET("posts/animals")
    suspend fun getAnimalPosts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<PostResponse>
    
    /**
     * 获取帖子详情 - 支持数字类型ID
     * @param postId 帖子的数字ID
     * @return 包含帖子详情的响应对象
     */
    @GET("posts/{id}")
    suspend fun getPostDetail(@Path("id") postId: Int): Response<Post>
    
    /**
     * 获取帖子详情 - 支持字符串类型ID
     * @param postId 帖子的字符串ID（如 post-plant-1）
     * @return 包含帖子详情的API响应对象
     */
    @GET("posts/{id}")
    suspend fun getPostDetail(@Path("id") postId: String): Response<ApiResponse<Post>>

    /**
     * 健康检查接口
     * 用于检查后端服务器的运行状态
     * @return 包含健康状态信息的响应对象
     */
    @GET("health")
    suspend fun healthCheck(): Response<HealthCheckResponse>

    // 伴生对象，提供静态方法和常量
    companion object {
        // 后端服务器的基础URL，使用Android模拟器的特殊IP地址
        private const val BASE_URL = "http://10.0.2.2:3000/api/"

        /**
         * 创建ApiService实例的工厂方法
         * 配置Retrofit客户端并返回ApiService接口的实现
         * @return 配置好的ApiService实例
         */
        fun create(): ApiService {
            // 构建Retrofit实例
            val retrofit = Retrofit.Builder()
                // 设置基础URL
                .baseUrl(BASE_URL)
                // 添加Gson转换器，用于JSON数据的序列化和反序列化
                .addConverterFactory(GsonConverterFactory.create())
                // 构建Retrofit实例
                .build()
            // 创建并返回ApiService接口的实现
            return retrofit.create(ApiService::class.java)
        }
    }
}