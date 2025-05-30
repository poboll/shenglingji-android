package com.venus.xiaohongshu.data.api

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.Arrays
import java.util.concurrent.TimeUnit

object RetrofitModule {
    private val TAG = "RetrofitModule"
    
    // 模拟器使用10.0.2.2访问宿主机的localhost
    private const val EMULATOR_BASE_URL = "http://10.0.2.2:3000/api/"
    
    // 真机使用开发机器在局域网中的实际IP地址，改为你电脑的局域网IP
    // 可以通过在电脑终端运行 "ipconfig" (Windows) 或 "ifconfig" (Mac/Linux) 获取
    private const val DEVICE_BASE_URL = "http://10.0.2.2:3000/api/"
    
    // 这里选择使用哪个URL，根据你的测试环境修改
    const val BASE_URL = EMULATOR_BASE_URL
    
    init {
        Log.d(TAG, "使用API基础URL: $BASE_URL")
    }
    
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val request = chain.request()
            Log.d(TAG, "发起请求: ${request.url}")
            try {
                val response = chain.proceed(request)
                Log.d(TAG, "收到响应: ${response.code} - ${request.url}")
                response
            } catch (e: Exception) {
                Log.e(TAG, "请求失败: ${e.message}", e)
                throw e
            }
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .protocols(Arrays.asList(Protocol.HTTP_1_1))
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
    
    val userApiService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }
} 