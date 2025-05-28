package com.caiths.shenglingji.ui.mine.composable

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil3.compose.AsyncImage
import com.caiths.shenglingji.R
import com.caiths.shenglingji.ui.mine.model.User
import com.caiths.shenglingji.ui.mine.viewmodel.MineViewModel

/**
 * Description: 个人中心顶部滚动内容
 *
 * @author: venus
 * @date: 2024/11/21
 */
@Composable
fun MineScrollingTopContent(
    vm: MineViewModel,
    user: User?,
    scrollState: ScrollState,
    onEditProfile: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            // 背景渐变
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF708090),
                                Color(0xFF2F4F4F)
                            )
                        )
                    )
            )
            
            // 用户信息
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 头像
                Image(
                    painter = painterResource(id = R.drawable.icon_mine),
                    contentDescription = "用户头像",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 用户名
                Text(
                    text = user?.nickname ?: user?.username ?: "动植物科普用户",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 用户简介
                Text(
                    text = "这个用户很懒，还没有填写简介",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 编辑资料按钮
                Button(
                    onClick = onEditProfile,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .width(120.dp)
                        .height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "编辑",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "编辑资料", fontSize = 12.sp)
                }
            }
        }
        
        // 用户统计信息卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(title = "收藏", count = "0")
                StatItem(title = "关注", count = "0")
                StatItem(title = "粉丝", count = "0")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun StatItem(title: String, count: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun IconTitleTextView(
    icon: Int,
    title: String,
    text: String,
    modifier: Modifier
) {
    ConstraintLayout(
        modifier = modifier
            .background(color = Color(0x20FFFFFF), shape = RoundedCornerShape(20))
            .padding(8.dp)
    ) { 
        val (iconRef, titleRef, textRef) = remember { createRefs() }
        
        AsyncImage(
            model = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
                .constrainAs(iconRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }
        )
        
        Text(
            text = title,
            fontSize = 11.sp,
            color = colorResource(R.color.theme_background_gray),
            modifier = Modifier.constrainAs(titleRef){
                start.linkTo(iconRef.end)
                top.linkTo(iconRef.top)
                bottom.linkTo(iconRef.bottom)
            }
                .padding(start = 2.dp)
        )

        Text(
            text = text,
            fontSize = 10.sp,
            color = Color(0x80FFFFFF),
            modifier = Modifier.constrainAs(textRef){
                start.linkTo(parent.start)
                top.linkTo(iconRef.bottom)
            }.padding(top = 2.dp)
        )
    }
}

@Composable
fun NumberTextView(
    number: Int,
    text: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) { 
        Text(
            text = "$number",
            fontSize = 10.sp,
            color = colorResource(R.color.theme_background_gray)
        )
        Text(
            text = text,
            fontSize = 10.sp,
            color = colorResource(R.color.theme_background_gray)
        )
    }
}

@Composable
fun AnimatedImage(
    image: Int,
    scroll: Float
) {
    val dynamicAnimationSizeValue = (initialImageFloat - scroll).coerceIn(36f, initialImageFloat)
    Image(
        painter = painterResource(id = image),
        contentScale = ContentScale.Crop,
        contentDescription = null,
        modifier = Modifier
            .size(animateDpAsState(Dp(dynamicAnimationSizeValue)).value)
            .clip(CircleShape)
    )
}