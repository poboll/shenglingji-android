package com.venus.xiaohongshu.ui.home.composable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.venus.xiaohongshu.R
import com.venus.xiaohongshu.ui.home.city.CityPage
import com.venus.xiaohongshu.ui.home.discovery.DiscoveryPage
import com.venus.xiaohongshu.ui.home.follow.QuizPage

/**
 * Description:
 *
 * @author: poboll
 * @date: 2024/05/26
 */
@Composable
fun HomeBody(
    pagerState: PagerState
) {
    Surface(
        color = colorResource(R.color.theme_background_gray)
    ) {
        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 1, // 预加载页数
            modifier = Modifier
                .fillMaxSize()
        ) { page ->
            when(page) {
                0 -> {
                    QuizPage()
                }

                1 -> {
                    DiscoveryPage()
                }

                2 -> {
                    CityPage()
                }
            }
        }
    }
}