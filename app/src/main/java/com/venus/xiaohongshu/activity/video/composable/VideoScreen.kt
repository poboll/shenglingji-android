package com.venus.xiaohongshu.activity.video.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.venus.xiaohongshu.activity.video.composable.VenusPlayer
import com.venus.xiaohongshu.R
import com.venus.xiaohongshu.activity.graphic.composable.IconNumberView
import com.venus.xiaohongshu.activity.video.VideoViewModel
import kotlin.random.Random

/**
 * Description:
 *
 * @Author: HuaJ1a
 * @Date: 2024/11/28
 */
@Composable
fun VideoScreen() {
    val vm = viewModel<VideoViewModel>()
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black)
                .padding(innerPadding)
        ) {
            when {
                vm.isLoading -> {
                    // 显示加载指示器
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = colorResource(R.color.theme_red))
                            Text(
                                text = "正在加载视频...",
                                color = Color.White,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }
                }
                vm.errorMessage != null -> {
                    // 显示错误信息
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "加载失败",
                                color = Color.White,
                                fontSize = 18.sp
                            )
                            Text(
                                text = vm.errorMessage ?: "未知错误",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
                vm.videoList.isNotEmpty() -> {
                    VideoBody(vm)
                }
                else -> {
                    // 空状态
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "暂无视频内容",
                            color = Color.White
                        )
                    }
                }
            }
            VideoTopBar()
        }
    }
}

@Composable
fun VideoBody(vm: VideoViewModel) {
    val context = LocalContext.current
    val videoList = vm.videoList
    val pagerState = rememberPagerState(pageCount = { videoList.size })
    var inputText by remember {
        mutableStateOf(TextFieldValue(""))
    }
    
    // 确保页面状态同步
    LaunchedEffect(videoList.size) {
        if (videoList.isNotEmpty()) {
            // 重置页面状态
            try {
                pagerState.scrollToPage(0)
            } catch (e: Exception) {
                // 忽略可能的异常
            }
        }
    }
    
    if (videoList.isEmpty()) {
        return
    }
    
    VerticalPager(
        state = pagerState
    ) { index ->
        if (index < 0 || index >= videoList.size) {
            // 处理索引越界情况
            return@VerticalPager
        }
        
        val item = videoList[index]
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier.weight(1f),
            ) {
                VenusPlayer(
                    context = context,
                    videoUrl = item.videoUrl,
                    rawResourceId = item.video
                )
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val avatarModel = item.user.userAvatar ?: item.user.image
                        AsyncImage(
                            model = avatarModel,
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Text(
                            text = item.user.name,
                            fontSize = 14.sp,
                            color = Color.White,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                        Text(
                            text = "关注",
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .background(
                                    color = colorResource(R.color.theme_red),
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(vertical = 4.dp, horizontal = 12.dp)
                        )
                    }
                    Text(
                        text = item.title,
                        color = Color.White,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    modifier = Modifier
                        .background(Color(0x50FFFFFF), RoundedCornerShape(50))
                        .weight(1f)
                        .height(30.dp),
                    value = inputText,
                    onValueChange = {
                        inputText = it
                    },
                    cursorBrush = SolidColor(colorResource(R.color.theme_red)),
                    decorationBox = { innerTextField ->
                        Row (
                            modifier = Modifier.fillMaxSize()
                                .padding(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = R.drawable.icon_write,
                                contentDescription = null,
                                modifier = Modifier.padding(start = 5.dp)
                                    .size(20.dp)
                            )
                            Box(
                                modifier = Modifier.padding(start = 5.dp)
                            ) {
                                if(inputText.text.isEmpty()) {
                                    Text(
                                        text = "说点什么...",
                                        color = colorResource(R.color.theme_text_gray),
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                innerTextField()
                            }
                        }
                    }
                )
                IconNumberView(image = R.drawable.icon_favorite_white, number = item.likes, textColor = Color.White)
                IconNumberView(image = R.drawable.icon_shoucang_white, number = Random.nextInt(100, 999), textColor = Color.White)
                IconNumberView(image = R.drawable.icon_pinglun_2_white, number = Random.nextInt(100, 999), textColor = Color.White)
            }
        }
    }
}