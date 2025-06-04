package com.venus.xiaohongshu.ui.home

import androidx.compose.runtime.Composable
import com.venus.xiaohongshu.base.BaseComposeFragment
import com.venus.xiaohongshu.ui.home.composable.HomeScreen

/**
 * Description:
 *
 * @author: poboll
 * @date: 2024/05/23
 */
class HomeFragment: BaseComposeFragment() {
    @Composable
    override fun ComposeContent() {
        HomeScreen()
    }
}