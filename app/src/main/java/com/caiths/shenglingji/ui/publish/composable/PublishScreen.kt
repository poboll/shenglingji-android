package com.caiths.shenglingji.ui.publish.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.caiths.shenglingji.ui.publish.album.AlbumPage
import com.caiths.shenglingji.ui.publish.capture.CapturePage
import com.caiths.shenglingji.ui.publish.live.LivePage
import com.caiths.shenglingji.ui.publish.template.TemplatePage
import com.caiths.shenglingji.ui.publish.text.TextPage
import com.caiths.shenglingji.ui.publish.viewmodel.PublishViewModel

/**
 * Description:
 *
 * @author: venus
 * @date: 2024/11/15
 */
@Composable
fun PublishScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "发布页面")
    }
}

