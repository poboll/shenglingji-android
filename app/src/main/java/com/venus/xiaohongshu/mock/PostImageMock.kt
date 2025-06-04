package com.venus.xiaohongshu.mock

import android.content.Context
import com.venus.xiaohongshu.data.PostImage
import kotlin.random.Random

/**
 * Description: 帖子图片模拟数据
 *
 * @author: poboll
 * @date: 2024/05/25
 */
object PostImageMock {
    
    /**
     * 提供随机图片列表
     * @param context 上下文
     * @param count 图片数量
     * @return 图片列表
     */
    fun provideRandomImages(context: Context, count: Int): List<PostImage> {
        val images = mutableListOf<PostImage>()
        repeat(count) { index ->
            // 在离线模式下，全部使用本地资源，确保图片能正常加载
            val imageUrl = "android.resource://${context.packageName}/${ImageMock.getRandomImage()}"
            
            images.add(
                PostImage(
                    id = "$index",
                    _imageUrl = imageUrl,
                    position = index,
                    description = if (Random.nextBoolean()) "图片描述 #${index + 1}" else null
                )
            )
        }
        return images
    }
}