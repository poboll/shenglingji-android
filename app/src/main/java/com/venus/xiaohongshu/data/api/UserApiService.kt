package com.venus.xiaohongshu.data.api

import com.venus.xiaohongshu.data.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface UserApiService {
    @POST("users/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("users/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @GET("users/profile")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<Map<String, User>>
    
    @PUT("users/profile")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Body request: UpdateUserRequest
    ): Response<Map<String, User>>
    
    @PUT("users/profile/details")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<Map<String, Profile>>
    
    @PUT("users/password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Response<Map<String, String>>
    
    @Multipart
    @POST("users/avatar")
    suspend fun uploadAvatar(
        @Header("Authorization") token: String,
        @Part avatar: MultipartBody.Part
    ): Response<Map<String, String>>
    
    @GET("utils/random-avatar")
    suspend fun getRandomAvatar(): Response<Map<String, String>>
    
    @GET("utils/random-nickname")
    suspend fun getRandomNickname(): Response<Map<String, String>>
} 