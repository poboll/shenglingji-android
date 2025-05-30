package com.venus.xiaohongshu.mock

import android.content.Context
import com.venus.xiaohongshu.data.PostImage
import kotlin.random.Random

/**
 * Description: 帖子图片模拟数据
 *
 * @author: venus
 * @date: 2024/11/30
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
            val imageUrl = when (Random.nextInt(3)) {
                0 -> "https://picsum.photos/800/600?random=${Random.nextInt(1000)}"
                1 -> "https://source.unsplash.com/random/800x600?sig=${Random.nextInt(1000)}"
                else -> "android.resource://${context.packageName}/${ImageMock.getRandomImage()}"
            }
            
            images.add(
                PostImage(
                    id = "$index",
                    imageUrl = imageUrl,
                    position = index,
                    description = if (Random.nextBoolean()) "图片描述 #${index + 1}" else null
                )
            )
        }
        return images
    }
} 