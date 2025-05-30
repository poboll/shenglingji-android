package com.venus.xiaohongshu.mock

import android.content.Context
import com.venus.xiaohongshu.R
import com.venus.xiaohongshu.data.model.PostVideo
import kotlin.random.Random

/**
 * Description:
 *
 * @author: venus
 * @date: 2024/11/22
 */
object VideoMock {
    
    private val videoList = listOf(
        R.raw.video_1,
        R.raw.video_2,
        R.raw.video_3
    )
    
    fun getRandomVideo(): Int {
        return videoList[Random.nextInt(videoList.size)]
    }
    
    // 提供随机视频对象
    fun provideRandomVideo(context: Context): PostVideo {
        val videoId = getRandomVideo()
        val videoUrl = "android.resource://${context.packageName}/${videoId}"
        // 封面使用视频的资源ID模拟
        val coverUrl = "drawable://${videoId}" 
        val duration = Random.nextInt(10, 120) // 10-120秒的随机时长
        
        return PostVideo(
            id = "0",
            videoUrl = videoUrl,
            coverUrl = coverUrl,
            duration = duration
        )
    }
}