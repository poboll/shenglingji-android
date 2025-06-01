package com.venus.xiaohongshu.network

import android.util.Log
import com.venus.xiaohongshu.app.AppApplication
import com.venus.xiaohongshu.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit客户端单例
 */
object RetrofitClient {
    // 设置后端服务器地址，请替换为你的实际后端地址
    private const val BASE_URL = "http://10.0.2.2:3000/api/" // 本地开发时，Android模拟器访问主机的地址，包含/api/路径
    private const val TAG = "RetrofitClient"
    
    // 创建SessionManager实例
    private val sessionManager by lazy { SessionManager(AppApplication.appContext) }
    
    // 创建授权拦截器，添加token到请求头
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        
        // 获取当前的token
        val token = sessionManager.getToken()
        
        // 如果有token，将其添加到请求头中
        val request = if (!token.isNullOrEmpty()) {
            Log.d(TAG, "Adding auth token to request")
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            Log.d(TAG, "No auth token available")
            originalRequest
        }
        
        chain.proceed(request)
    }
    
    // 创建OkHttpClient
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor) // 添加授权拦截器
        .addInterceptor(HttpLoggingInterceptor { message ->
            Log.d(TAG, message) // 打印所有网络日志
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()
    
    // 创建Retrofit实例
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    // 创建API服务
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}