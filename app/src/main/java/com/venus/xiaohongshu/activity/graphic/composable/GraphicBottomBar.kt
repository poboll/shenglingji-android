package com.venus.xiaohongshu.activity.graphic.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.venus.xiaohongshu.R
import com.venus.xiaohongshu.activity.graphic.GraphicViewModel
import kotlin.random.Random

/**
 * Description:
 *
 * @author: poboll
 * @date: 2024/05/27
 */
@Composable
fun GraphicBottomBar(vm: GraphicViewModel) {
    var inputText by remember {
        mutableStateOf(TextFieldValue(""))
    }
    val focusRequester = remember { FocusRequester() }
    
    // 监听焦点状态变化
    LaunchedEffect(vm.shouldFocusCommentInput) {
        if (vm.shouldFocusCommentInput) {
            focusRequester.requestFocus()
            vm.resetCommentInputFocus()
        }
    }
    
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            modifier = Modifier
                .background(colorResource(R.color.theme_background_gray), RoundedCornerShape(50))
                .weight(1f)
                .height(30.dp)
                .focusRequester(focusRequester),
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
        // 点赞按钮
        ClickableIconNumberView(
            image = if (vm.isLiked) R.drawable.icon_favorite_filled else R.drawable.icon_favorite_black,
            number = vm.graphicPost?.likes ?: 0,
            isActive = vm.isLiked,
            activeColor = Color.Red,
            onClick = { vm.likePost() }
        )
        
        // 收藏按钮
        ClickableIconNumberView(
            image = if (vm.isBookmarked) R.drawable.icon_shoucang_filled else R.drawable.icon_shoucang,
            number = Random.nextInt(100, 999),
            isActive = vm.isBookmarked,
            activeColor = Color(0xFFFFD700), // 金黄色
            onClick = { vm.bookmarkPost() }
        )
        
        // 评论按钮
        ClickableIconNumberView(
            image = R.drawable.icon_pinglun_2,
            number = vm.comments.size,
            isActive = false,
            activeColor = Color.Gray,
            onClick = { vm.focusCommentInput() }
        )
    }
}

@Composable
fun IconNumberView(
    image: Int,
    number: Int,
    textColor: Color = Color.Unspecified,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 10.dp)
    ) {
        AsyncImage(
            model = image,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = "${number}",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp),
            color = textColor
        )
    }
}

@Composable
fun ClickableIconNumberView(
    image: Int,
    number: Int,
    isActive: Boolean = false,
    activeColor: Color = Color.Red,
    textColor: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(start = 10.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = image,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = "${number}",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp),
            color = if (isActive) activeColor else textColor
        )
    }
}
