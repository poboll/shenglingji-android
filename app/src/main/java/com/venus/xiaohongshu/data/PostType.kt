// 定义包名，标识数据模型类的命名空间
package com.venus.xiaohongshu.data

/**
 * 帖子类型枚举类
 * 用于定义应用中支持的帖子媒体类型
 * 提供类型安全的帖子分类方式
 */
enum class PostType {
    // 图片类型帖子，包含一张或多张图片
    IMAGE, 
    // 视频类型帖子，包含视频内容
    VIDEO
}