package com.venus.xiaohongshu.activity.graphic.composable

import androidx.compose.foundation.background
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
import kotlin.random.Random

/**
 * Description:
 *
 * @Author: HuaJ1a
 * @Date: 2024/11/28
 */
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
            val imageModel = postData.imageUrl ?: postData.image
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
                val authorAvatarModel = postData.user.userAvatar ?: postData.user.image
                AsyncImage(
                    model = authorAvatarModel,
                    contentDescription = "作者头像",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
                Text(
                    text = postData.user.userName ?: postData.user.name,
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
            
            Text(
                text = "发布于: ${postData.createdAt}",
                fontSize = 12.sp,
                color = colorResource(R.color.theme_text_gray),
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
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
                    )
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
                        text = "评论于: ${commentData.createdAt}",
                        fontSize = 10.sp,
                        color = colorResource(R.color.theme_text_gray)
                    )
                }
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