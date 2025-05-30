package com.venus.xiaohongshu.activity.graphic.composable

import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
 * @Author: HuaJ1a
 * @Date: 2024/11/28
 */
@Composable
fun GraphicBottomBar(vm: GraphicViewModel) {
    var inputText by remember {
        mutableStateOf(TextFieldValue(""))
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
        IconNumberView(image = R.drawable.icon_favorite_black, number = vm.graphicPost?.likes ?: 0)
        IconNumberView(image = R.drawable.icon_shoucang, number = Random.nextInt(100, 999))
        IconNumberView(image = R.drawable.icon_pinglun_2, number = vm.comments.size)
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
