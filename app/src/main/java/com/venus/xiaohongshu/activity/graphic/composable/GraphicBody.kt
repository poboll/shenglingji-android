package com.venus.xiaohongshu.activity.graphic.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.venus.xiaohongshu.R
import com.venus.xiaohongshu.activity.graphic.GraphicViewModel
import com.venus.xiaohongshu.ui.common.Divider
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

/**
 * Description:
 *
 * @Author: HuaJ1a
 * @Date: 2024/11/28
 */

/**
 * 格式化时间显示
 * 将ISO时间格式转换为友好的显示格式
 */
fun formatTimeDisplay(isoTimeString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = inputFormat.parse(isoTimeString) ?: return "未知时间"
        
        val now = Calendar.getInstance()
        val postTime = Calendar.getInstance().apply { time = date }
        
        val diffInMillis = now.timeInMillis - postTime.timeInMillis
        val diffInMinutes = diffInMillis / (1000 * 60)
        val diffInHours = diffInMillis / (1000 * 60 * 60)
        val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)
        
        when {
            diffInMinutes < 1 -> "刚刚"
            diffInMinutes < 60 -> "${diffInMinutes}分钟前"
            diffInHours < 24 && now.get(Calendar.DAY_OF_YEAR) == postTime.get(Calendar.DAY_OF_YEAR) -> {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                "今天 ${timeFormat.format(date)}"
            }
            diffInDays == 1L -> {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                "昨天 ${timeFormat.format(date)}"
            }
            diffInDays < 7 -> "${diffInDays}天前"
            else -> {
                val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                dateFormat.format(date)
            }
        }
    } catch (e: Exception) {
        "时间解析错误"
    }
}

@Composable
fun GraphicBody(vm: GraphicViewModel, modifier: Modifier) {
    var inputText by remember {
        mutableStateOf(TextFieldValue(""))
    }
    
    // 加载帖子详情
    LaunchedEffect(key1 = vm.id) {
        if (vm.id.isNotEmpty()) {
            vm.loadPostDetail(vm.id)
        }
    }
    
    if (vm.isLoading && vm.graphicPost == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                color = colorResource(id = R.color.theme_red)
            )
        }
        return
    }
    
    if (vm.errorMessage != null && vm.graphicPost == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "加载失败: ${vm.errorMessage}",
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        }
        return
    }
    
    val postData = vm.graphicPost
    
    if (postData == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "帖子数据不可用。",
                modifier = Modifier.padding(16.dp)
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier
    ) {
        item {
            val imageModel = postData.getDisplayCover()
            AsyncImage(
                model = imageModel,
                contentDescription = "帖子图片",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .background(Color.Black),
                contentScale = ContentScale.Fit
            )
        }

        // 帖子作者信息和主点赞
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = postData.author.avatar,
                    contentDescription = "作者头像",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
                Text(
                    text = postData.author.username,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Spacer(Modifier.weight(1f))
                AsyncImage(
                    model = R.drawable.icon_favorite_black,
                    contentDescription = "点赞图标",
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = postData.likes.toString(),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        // 文案
        item {
            Text(
                text = postData.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 8.dp),
                textAlign = TextAlign.Start,
                fontSize = 16.sp
            )
            
            if (!postData.content.isNullOrEmpty()) {
                Text(
                    text = postData.content,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                    textAlign = TextAlign.Start,
                    fontSize = 14.sp
                )
            }
            
            // 发布时间和IP归属地
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatTimeDisplay(postData.createdAt),
                    fontSize = 12.sp,
                    color = colorResource(R.color.theme_text_gray)
                )
                
                // IP归属地显示
                if (!postData.location.isNullOrEmpty()) {
                    Text(
                        text = " · ",
                        fontSize = 12.sp,
                        color = colorResource(R.color.theme_text_gray)
                    )
                    Text(
                        text = "IP归属地：${postData.location}",
                        fontSize = 12.sp,
                        color = colorResource(R.color.theme_text_gray)
                    )
                }
            }
        }

        item {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Divider()
                Text(
                    text = "共${vm.comments.size}条评论",
                    fontSize = 12.sp,
                    color = colorResource(R.color.theme_text_gray),
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    AsyncImage(
                        model = R.drawable.icon_cake,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                    )
                    BasicTextField(
                        modifier = Modifier
                            .background(colorResource(R.color.theme_background_gray), RoundedCornerShape(50))
                            .weight(1f)
                            .height(35.dp),
                        value = inputText,
                        onValueChange = {
                            inputText = it
                        },
                        cursorBrush = SolidColor(colorResource(R.color.theme_red)),
                        decorationBox = { innerTextField ->
                            Row (
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 10.dp)
                                ) {
                                    if(inputText.text.isEmpty()) {
                                        Text(
                                            text = "爱评论的人运气都不差",
                                            color = colorResource(R.color.theme_text_gray),
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    innerTextField()
                                }
                                
                                // 添加发送按钮，会在输入内容后显示
                                if (inputText.text.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .padding(start = 8.dp, end = 8.dp)
                                            .clickable(enabled = !vm.isSubmittingComment) {
                                                if (inputText.text.isNotEmpty()) {
                                                    vm.postComment(inputText.text)
                                                    // 成功提交后清空输入框
                                                    inputText = TextFieldValue("")
                                                }
                                            }
                                    ) {
                                        if (vm.isSubmittingComment) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                strokeWidth = 2.dp,
                                                color = colorResource(R.color.theme_red)
                                            )
                                        } else {
                                            Text(
                                                text = "发送",
                                                color = colorResource(R.color.theme_red),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                } else {
                                    // 当没有内容时显示表情和图片按钮
                                    AsyncImage(
                                        model = R.drawable.icon_aite,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(start = 10.dp)
                                            .size(20.dp)
                                    )
                                    AsyncImage(
                                        model = R.drawable.icon_smile,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(start = 10.dp)
                                            .size(20.dp)
                                    )
                                    AsyncImage(
                                        model = R.drawable.icon_picture,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(start = 10.dp, end = 10.dp)
                                            .size(20.dp)
                                    )
                                }
                            }
                        }
                    )
                }
                
                // 显示评论错误信息
                if (vm.commentErrorMessage != null) {
                    Text(
                        text = vm.commentErrorMessage ?: "",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // 评论加载状态
        if (vm.isCommentsLoading) {
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = colorResource(id = R.color.theme_red)
                    )
                }
            }
        }

        // 评论为空时的提示
        if (!vm.isCommentsLoading && vm.comments.isEmpty()) {
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp, horizontal = 16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🌟",
                            fontSize = 32.sp
                        )
                        Text(
                            text = "现在还没有人评论哦~",
                            fontSize = 14.sp,
                            color = colorResource(R.color.theme_text_gray),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "快来抢沙发吧！",
                            fontSize = 12.sp,
                            color = colorResource(R.color.theme_text_gray),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // 评论
        items(items = vm.comments) { commentData ->
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                val avatarModel = commentData.user.userAvatar ?: commentData.user.image
                AsyncImage(
                    model = avatarModel,
                    contentDescription = "评论用户头像",
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(26.dp)
                        .clip(CircleShape)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = commentData.user.userName ?: commentData.user.name,
                        color = colorResource(R.color.theme_text_gray),
                        fontSize = 12.sp
                    )
                    Text(
                        text = commentData.content ?: commentData.title,
                        fontSize = 14.sp
                    )
                    Text(
                        text = formatTimeDisplay(commentData.createdAt),
                        fontSize = 10.sp,
                        color = colorResource(R.color.theme_text_gray)
                    )
                }
                Box(
                    modifier = Modifier
                        .clickable { vm.likeComment(commentData.id) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(
                            model = R.drawable.icon_favorite_black,
                            contentDescription = "评论点赞图标",
                            modifier = Modifier
                                .size(16.dp)
                                .padding(start = 10.dp)
                        )
                        Text(
                            text = commentData.likes.toString(),
                            fontSize = 10.sp,
                            color = colorResource(R.color.theme_text_gray),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}