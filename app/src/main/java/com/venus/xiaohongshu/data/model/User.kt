package com.venus.xiaohongshu.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("avatar") val avatar: String? = null,
    // Add other fields from API if needed, e.g., nickname, bio, etc.
    // Mock data fields for compatibility
    val userId: String? = null, // from mock, maps to id
    val userAvatar: String? = null, // from mock, maps to avatar
    val userName: String? = null, // from mock, maps to username
    val email: String,
    val nickname: String?,
    val bio: String?,
    val phone: String?,
    val gender: String?,
    val birthday: String?,
    val status: String?,
    val createdAt: String?
) {
    fun getDisplayUsername(): String {
        return username ?: userName ?: ""
    }
    fun getDisplayAvatar(): String? {
        return (avatar ?: userAvatar)?.trim()
    }
}

data class Profile(
    val userId: Int,
    val location: String?,
    val website: String?,
    val education: String?,
    val occupation: String?,
    val interests: String?,
    val socialLinks: Map<String, String>?,
    val preferences: Map<String, Any>?
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val nickname: String?,
    val phone: String?
)

data class AuthResponse(
    val message: String,
    val user: User,
    val token: String
)

data class UpdateUserRequest(
    val nickname: String?,
    val bio: String?,
    val avatar: String?,
    val gender: String?,
    val birthday: String?,
    val phone: String?
)

data class UpdateProfileRequest(
    val location: String?,
    val website: String?,
    val education: String?,
    val occupation: String?,
    val interests: String?,
    val socialLinks: Map<String, String>?,
    val preferences: Map<String, Any>?
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)