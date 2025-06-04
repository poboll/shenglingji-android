package com.venus.xiaohongshu.activity.video.composable

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.annotation.RawRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
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
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.venus.xiaohongshu.R
import kotlinx.coroutines.delay

/**
 * Enhanced VenusPlayer with improved scrolling behavior and UI controls
 *
 * @author: poboll
 * @date: 2024/05/26
 */
@OptIn(UnstableApi::class)
@Composable
fun VenusPlayer(
    context: Context = LocalContext.current,
    videoUrl: String?,
    @RawRes rawResourceId: Int? = null,
    isCurrentlyVisible: Boolean = true,
    autoPlay: Boolean = true
) {
    val TAG = "VenusPlayer"

    // 状态跟踪
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isPlayerReady by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(false) }
    var retryCount by remember { mutableStateOf(0) }
    val MAX_RETRY = 3
    
    // 可见性状态更新，确保我们使用最新的可见性值
    val isVisibleState = rememberUpdatedState(isCurrentlyVisible)
    
    // 视频和控制状态
    val isVideoFullyLoaded = remember { mutableStateOf(false) }

    // 生命周期监听
    val lifecycleOwner = LocalLifecycleOwner.current

    // 记录视频信息用于调试
    Log.d(TAG, "VenusPlayer 初始化: videoUrl=$videoUrl, rawResourceId=$rawResourceId, visible=$isCurrentlyVisible")

    // 设置备用视频资源
    val fallbackVideoResource = R.raw.video_1

    // 确定要使用的视频资源
    val effectiveResourceId = when {
        rawResourceId != null && rawResourceId != 0 -> rawResourceId
        else -> fallbackVideoResource
    }

    // 创建播放器实例，添加配置以优化瀑布流中的使用
    val trackSelector = DefaultTrackSelector(context).apply {
        // 设置较低的视频缓冲参数，提高响应性
        setParameters(buildUponParameters()
            .setMaxVideoBitrate(2_000_000) // 限制视频比特率，避免使用过高清晰度
            .setMaxVideoSize(1280, 720)    // 限制视频分辨率
        )
    }
    
    val player = remember {
        Log.d(TAG, "创建ExoPlayer实例")
        ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .setHandleAudioBecomingNoisy(true) // 处理音频中断
            .setSeekBackIncrementMs(5000) // 后退增量
            .setSeekForwardIncrementMs(5000) // 前进增量
            .build()
            .apply {
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                repeatMode = Player.REPEAT_MODE_ONE
                playWhenReady = false // 先不自动播放，等准备好再播放
                volume = 1.0f // 初始音量
                
                // 优化设置
                setPlaybackSpeed(1.0f)
                setForegroundMode(true) // 前台模式优化
            }
    }

    // 加载视频的统一函数
    fun loadVideo() {
        try {
            Log.d(TAG, "开始加载视频")
            isLoading = true
            hasError = false
            isPlayerReady = false
            isVideoFullyLoaded.value = false
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
                // 设置播放参数
                setPlaybackParameters(androidx.media3.common.PlaybackParameters(1.0f, 1.0f))
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

    // 状态自检函数
    fun performStateCheck() {
        Log.d(TAG, "执行状态自检: isPlayerReady=$isPlayerReady, isPlaying=${player.isPlaying}, playbackState=${player.playbackState}")
        
        if (isPlayerReady && !isPlaying && isVisibleState.value && !player.isPlaying && player.playbackState == Player.STATE_READY) {
            Log.d(TAG, "状态自检: 应该播放但未播放，恢复播放")
            player.play()
            isPlaying = true
        } else if ((player.playbackState == Player.STATE_IDLE || player.playbackState == Player.STATE_ENDED) && isPlayerReady && retryCount < MAX_RETRY) {
            Log.d(TAG, "状态自检: 播放器处于IDLE或ENDED状态，尝试恢复，重试次数: $retryCount")
            retryCount++
            loadVideo()
        }
    }

    // 切换播放/暂停状态
    fun togglePlayback() {
        if (isPlayerReady) {
            if (isPlaying) {
                player.pause()
                isPlaying = false
                Log.d(TAG, "暂停播放")
            } else {
                player.play()
                isPlaying = true
                Log.d(TAG, "恢复播放")
            }
            // 短暂显示控制UI
            showControls = true
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
                        isVideoFullyLoaded.value = true
                        
                        // 自动播放仅在可见且设置为自动播放时启动
                        if (isVisibleState.value && autoPlay) {
                            player.playWhenReady = true
                            isPlaying = true
                            Log.d(TAG, "自动开始播放")
                        }
                    }
                    Player.STATE_BUFFERING -> {
                        Log.d(TAG, "视频缓冲中")
                        isLoading = true
                    }
                    Player.STATE_ENDED -> {
                        Log.d(TAG, "视频播放结束")
                        isLoading = false
                        // 循环播放
                        player.seekTo(0)
                        player.play()
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
                if (!videoUrl.isNullOrEmpty()) {
                    Log.d(TAG, "网络视频失败，尝试本地视频")
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
            
            override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                // 同步播放状态
                isPlaying = isPlayingNow
                Log.d(TAG, "播放状态变化: isPlaying=$isPlayingNow")
            }
        }
        
        player.addListener(listener)
    }

    // 监听可见性变化
    LaunchedEffect(isVisibleState.value) {
        Log.d(TAG, "可见性变化: ${isVisibleState.value}")
        if (isVisibleState.value) {
            if (isPlayerReady && !player.isPlaying) {
                // 从不可见变为可见，恢复播放
                player.play()
                isPlaying = true
                Log.d(TAG, "恢复播放")
            }
        } else {
            // 从可见变为不可见，暂停播放
            if (player.isPlaying) {
                player.pause()
                isPlaying = false
                Log.d(TAG, "暂停播放")
            }
        }
        
        // 执行状态自检
        performStateCheck()
    }

    // 监听生命周期
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    Log.d(TAG, "生命周期暂停，暂停播放器")
                    player.pause()
                    isPlaying = false
                }
                Lifecycle.Event.ON_RESUME -> {
                    Log.d(TAG, "生命周期恢复")
                    if (isVisibleState.value && isPlayerReady) {
                        player.play()
                        isPlaying = true
                        Log.d(TAG, "生命周期恢复，恢复播放")
                    }
                }
                else -> {}
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

    // 自动隐藏控制按钮
    LaunchedEffect(showControls) {
        if (showControls) {
            delay(1500) // 1.5秒后自动隐藏控制
            showControls = false
        }
    }

    // 定期状态自检
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000) // 每5秒检查一次
            performStateCheck()
        }
    }

    // 超时处理
    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(15000) // 延长超时时间到15秒
            if (isLoading && !hasError && !isPlayerReady) {
                Log.w(TAG, "视频加载超时")
                errorMessage = "视频加载超时，请检查网络连接"
                hasError = true
                isLoading = false
                
                // 超时后尝试本地视频
                if (!videoUrl.isNullOrEmpty()) {
                    Log.d(TAG, "尝试加载本地视频")
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
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { 
                // 点击直接切换播放/暂停状态
                togglePlayback()
            },
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
                    
                    // 优化设置
                    setKeepContentOnPlayerReset(true)
                    setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                    // 启用硬件加速
                    setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 加载状态覆盖层
        if (isLoading && !hasError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
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
                    .background(Color.Black.copy(alpha = 0.7f)),
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
                        modifier = Modifier.padding(top = 8.dp),
                        textAlign = TextAlign.Center
                    )
                    
                    // 重试按钮
                    IconButton(
                        onClick = { 
                            hasError = false
                            isLoading = true
                            loadVideo()
                        },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .size(48.dp)
                            .background(Color.White.copy(alpha = 0.2f), shape = androidx.compose.foundation.shape.CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_refresh),
                            contentDescription = "重试",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
        
        // 播放控制层 - 当点击时短暂显示
        AnimatedVisibility(
            visible = showControls && isPlayerReady && !isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                // 标准播放/暂停按钮
                IconButton(
                    onClick = { togglePlayback() },
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
                        ),
                        contentDescription = if (isPlaying) "暂停" else "播放",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(32.dp)
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