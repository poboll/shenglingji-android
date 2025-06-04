// 定义包名，标识数据模型类的命名空间
package com.venus.xiaohongshu.data.model

// 导入Gson序列化注解，用于JSON字段映射
import com.google.gson.annotations.SerializedName

/**
 * 健康检查响应数据类
 * 用于封装服务器健康检查接口的响应数据
 * 通常用于检测API服务是否正常运行
 * 
 * @property status 服务状态，如"ok"、"error"等
 * @property message 状态描述信息，提供详细的状态说明
 */
data class HealthCheckResponse(
    // 使用SerializedName注解映射JSON字段"status"到Kotlin属性
    @SerializedName("status") val status: String,
    // 使用SerializedName注解映射JSON字段"message"到Kotlin属性
    @SerializedName("message") val message: String
)