package com.venus.xiaohongshu.activity.video.composable

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.venus.xiaohongshu.R
import kotlinx.coroutines.delay

/**
 * Description:
 *
 * @Author: HuaJ1a
 * @Date: 2024/11/28
 */
@OptIn(UnstableApi::class)
@Composable
fun VenusPlayer(context: Context, videoUrl: String?, @RawRes rawResourceId: Int?) {
    val TAG = "VenusPlayer"

    // 状态跟踪
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isPlayerReady by remember { mutableStateOf(false) }

    // 生命周期监听
    val lifecycleOwner = LocalLifecycleOwner.current

    // 记录视频信息用于调试
    Log.d(TAG, "VenusPlayer 初始化: videoUrl=$videoUrl, rawResourceId=$rawResourceId")

    // 设置备用视频资源
    val fallbackVideoResource = R.raw.video_1

    // 确定要使用的视频资源
    val effectiveResourceId = when {
        rawResourceId != null && rawResourceId != 0 -> rawResourceId
        else -> fallbackVideoResource
    }

    // 创建播放器实例
    val player = remember {
        Log.d(TAG, "创建ExoPlayer实例")
        ExoPlayer.Builder(context)
            .build()
            .apply {
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                repeatMode = Player.REPEAT_MODE_ONE
                playWhenReady = false // 先不自动播放，等准备好再播放
            }
    }

    // 加载视频的统一函数
    fun loadVideo() {
        try {
            Log.d(TAG, "开始加载视频")
            isLoading = true
            hasError = false
            isPlayerReady = false
            errorMessage = ""

            val mediaItem = if (!videoUrl.isNullOrEmpty()) {
                Log.d(TAG, "使用网络视频: $videoUrl")
                MediaItem.Builder().setUri(videoUrl).build()
            } else {
                val uri = Uri.parse("android.resource://${context.packageName}/$effectiveResourceId")
                Log.d(TAG, "使用本地视频: $uri")
                MediaItem.Builder().setUri(uri).build()
            }

            player.apply {
                stop()
                clearMediaItems()
                setMediaItem(mediaItem)
                prepare()
            }

            Log.d(TAG, "视频媒体项设置完成，等待播放器准备")

        } catch (e: Exception) {
            Log.e(TAG, "加载视频失败: ${e.message}", e)
            errorMessage = "加载视频失败: ${e.message}"
            hasError = true
            isLoading = false
        }
    }

    // 添加播放器监听器
    LaunchedEffect(player) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                Log.d(TAG, "播放状态变化: $playbackState")
                when (playbackState) {
                    Player.STATE_READY -> {
                        Log.d(TAG, "视频已准备好播放")
                        isLoading = false
                        hasError = false
                        isPlayerReady = true
                        // 开始播放
                        player.playWhenReady = true
                    }
                    Player.STATE_BUFFERING -> {
                        Log.d(TAG, "视频缓冲中")
                        isLoading = true
                    }
                    Player.STATE_ENDED -> {
                        Log.d(TAG, "视频播放结束")
                        isLoading = false
                    }
                    Player.STATE_IDLE -> {
                        Log.d(TAG, "播放器空闲")
                    }
                }
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                val errorMsg = "播放错误: ${error.message}"
                Log.e(TAG, errorMsg, error)
                errorMessage = errorMsg
                hasError = true
                isLoading = false
                isPlayerReady = false
                
                // 如果网络视频失败，尝试本地视频
                if (!videoUrl.isNullOrEmpty() && !hasError) {
                    Log.d(TAG, "网络视频失败，尝试本地视频")
                    hasError = false
                    isLoading = true
                    val uri = Uri.parse("android.resource://${context.packageName}/$effectiveResourceId")
                    val mediaItem = MediaItem.Builder().setUri(uri).build()
                    player.apply {
                        stop()
                        clearMediaItems()
                        setMediaItem(mediaItem)
                        prepare()
                    }
                }
            }
        }
        
        player.addListener(listener)
    }

    // 监听生命周期
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                Log.d(TAG, "生命周期暂停，暂停播放器")
                player.pause()
            } else if (event == Lifecycle.Event.ON_RESUME) {
                Log.d(TAG, "生命周期恢复，恢复播放器")
                player.play()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // 初始加载视频
    LaunchedEffect(videoUrl, effectiveResourceId) {
        Log.d(TAG, "LaunchedEffect 触发，开始加载视频")
        loadVideo()
    }

    // 超时处理
    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(10000) // 10秒超时
            if (isLoading && !hasError && !isPlayerReady) {
                Log.w(TAG, "视频加载超时")
                errorMessage = "视频加载超时"
                hasError = true
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx ->
                Log.d(TAG, "创建PlayerView")
                PlayerView(ctx).apply {
                    this.player = player
                    useController = false // 禁用默认控制器，使用自定义UI
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER) // 使用自定义加载指示器
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 加载状态覆盖层
        if (isLoading && !hasError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "加载中...",
                        color = Color.White,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }

        // 错误状态覆盖层
        if (hasError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "播放失败",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                    Text(
                        text = errorMessage,
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }

    // 清理资源
    DisposableEffect(Unit) {
        onDispose {
            Log.d(TAG, "释放播放器资源")
            player.release()
        }
    }
}