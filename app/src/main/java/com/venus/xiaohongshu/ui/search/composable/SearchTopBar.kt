package com.venus.xiaohongshu.ui.search.composable

import android.app.Activity
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.venus.xiaohongshu.R
import com.venus.xiaohongshu.ui.common.BackButton

/**
 * 搜索顶部栏
 * 
 * @param initialQuery 初始搜索关键词
 * @param onSearch 搜索回调
 * @param placeholderText 占位文本
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchTopBar(
    initialQuery: String = "",
    onSearch: (String) -> Unit = {},
    placeholderText: String = "空气凤梨养护指南",
    onBackClick: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    
    var inputText by remember {
        mutableStateOf(TextFieldValue(initialQuery))
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackButton {
            onBackClick()
            (context as? Activity)?.finish()
        }

        BasicTextField(
            modifier = Modifier
                .background(colorResource(R.color.theme_background_gray), RoundedCornerShape(50))
                .weight(1f)
                .height(35.dp)
                .focusRequester(focusRequester),
            value = inputText,
            onValueChange = {
                inputText = it
            },
            cursorBrush = SolidColor(colorResource(R.color.theme_red)),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (inputText.text.isNotEmpty()) {
                        onSearch(inputText.text)
                        keyboardController?.hide()
                    }
                }
            ),
            decorationBox = { innerTextField ->
                Row (
                    modifier = Modifier.fillMaxSize()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = R.drawable.icon_search,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 5.dp)
                            .size(24.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 5.dp)
                    ) {
                        if(inputText.text.isEmpty()) {
                            Text(
                                text = placeholderText,
                                color = colorResource(R.color.theme_text_gray)
                            )
                        }
                        innerTextField()
                    }
                    
                    // 显示清除按钮
                    if (inputText.text.isNotEmpty()) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_close),
                            contentDescription = "清除",
                            tint = colorResource(R.color.theme_text_gray),
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .size(16.dp)
                                .clickable {
                                    inputText = TextFieldValue("")
                                }
                        )
                    }
                }
            }
        )

        Text(
            text = "搜索",
            color = if (inputText.text.isNotEmpty()) colorResource(R.color.theme_red) else colorResource(R.color.theme_text_gray),
            modifier = Modifier
                .padding(start = 12.dp, end = 8.dp)
                .alpha(if (inputText.text.isEmpty()) 0.7f else 1.0f)
                .clickable {
                    if (inputText.text.isNotEmpty()) {
                        onSearch(inputText.text)
                        keyboardController?.hide()
                    }
                }
        )
    }
}