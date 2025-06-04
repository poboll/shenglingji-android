package com.venus.xiaohongshu.activity.graphic.composable

import android.app.Activity
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil3.compose.AsyncImage
import com.venus.xiaohongshu.R
import com.venus.xiaohongshu.activity.graphic.GraphicViewModel
import com.venus.xiaohongshu.ui.common.BackButton

/**
 * Description:
 *
 * @author: poboll
 * @date: 2024/06/03
 */
@Composable
fun GraphicTopBar(vm: GraphicViewModel, showBadge: Boolean = false) {
    val context = LocalContext.current
    
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
    ) {
        val (rowRef, badgeRef) = remember { createRefs() }
        
        // 主要工具栏内容
        Row(
            modifier = Modifier
                .constrainAs(rowRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .fillMaxWidth()
                .padding(start = 8.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton {
                (context as Activity).finish()
            }
            AsyncImage(
                model = vm.graphicPost?.author?.avatar?.trim(),
                contentDescription = "作者头像",
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(32.dp)
                    .clip(CircleShape)
            )
            Text(
                text = vm.graphicPost?.author?.username ?: "用户",
                modifier = Modifier.padding(start = 10.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            
            // 如果应该显示勋章，给关注按钮添加额外的边距
            val followPadding = if (showBadge) 16.dp else 8.dp
            
            Text(
                text = "关注",
                color = colorResource(R.color.theme_red),
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(start = 16.dp, end = followPadding)
                    .border(
                        width = 1.dp,
                        color = colorResource(R.color.theme_red),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(vertical = 4.dp, horizontal = 12.dp)
            )
            AsyncImage(
                model = R.drawable.icon_share,
                contentDescription = null,
                modifier = Modifier
                    .size(22.dp)
            )
        }
        
        // 勋章组件
        if (showBadge) {
            BadgeComponent(
                modifier = Modifier
                    .constrainAs(badgeRef) {
                        top.linkTo(rowRef.bottom, margin = 8.dp)
                        end.linkTo(parent.end, margin = 16.dp)
                    }
            )
        }
    }
}

/**
 * 勋章展示组件
 */
@Composable
fun BadgeComponent(modifier: Modifier = Modifier) {
    val isExpanded = remember { mutableStateOf(false) }
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = R.drawable.icon_fire,
            contentDescription = "观鸟达人勋章",
            modifier = Modifier
                .size(24.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFFFFD700),
                    shape = CircleShape
                )
                .padding(4.dp)
        )
        
        if (isExpanded.value) {
            Text(
                text = "观鸟达人",
                color = Color(0xFFFFD700),
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(start = 4.dp)
            )
        }
    }
}