package com.caiths.shenglingji.activity.video.composable

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.caiths.shenglingji.R
import com.caiths.shenglingji.activity.graphic.composable.IconNumberView
import com.caiths.shenglingji.activity.video.VideoViewModel
// import com.caiths.shenglingji.ui.home.bean.UserBean // UserBean might not be directly used here or item structure changed
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
            VideoBody(vm)
            VideoTopBar()
        }
    }
}

@Composable
fun VideoBody(vm: VideoViewModel) {
    val context = LocalContext.current
    val pagerState = rememberPagerState { vm.videoList.size }
    var inputText by remember {
        mutableStateOf(TextFieldValue(""))
    }
    VerticalPager(
        state = pagerState
    ) { page -> // Renamed 'it' to 'page' for clarity
        val item = vm.videoList[page] // Use 'page'
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier.weight(1f),
            ) {
                // Assuming VenusPlayer expects a URL or resource ID from item.imageUrl for video
                // and item.imageUrl for a poster image
                VenusPlayer(
                    context = context,
                    videoUrl = item.imageUrl.toInt(), // Assuming video URL is in imageUrl
                    posterUrl = item.imageUrl.toInt() // Assuming poster is also in imageUrl or a default
                )
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = "https://picsum.photos/seed/user${Random.nextInt()}/100/100", // Placeholder for user image
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Text(
                            text = "用户${Random.nextInt(1000, 9999)}", // Placeholder for user name
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
                IconNumberView(image = R.drawable.icon_favorite_white, number = Random.nextInt(0, 999), textColor = Color.White) // Placeholder for likes
                IconNumberView(image = R.drawable.icon_shoucang_white, number = Random.nextInt(100, 999), textColor = Color.White)
                IconNumberView(image = R.drawable.icon_pinglun_2_white, number = Random.nextInt(100, 999), textColor = Color.White)
            }
        }
    }
}

// Dummy VenusPlayer for compilation
@Composable
fun VenusPlayer(context: android.content.Context, videoUrl: Int, posterUrl: Int) {
    Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray)) {
        Text("Video Player", color = Color.White, modifier = Modifier.align(Alignment.Center))
    }
}