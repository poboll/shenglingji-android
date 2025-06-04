package com.venus.xiaohongshu.ui.mine

import androidx.compose.runtime.Composable
import com.venus.xiaohongshu.base.BaseComposeFragment
import com.venus.xiaohongshu.ui.mine.composable.MineScreen

/**
 * Description:
 *
 * @author: poboll
 * @date: 2024/05/30
 */
class MineFragment: BaseComposeFragment() {
    @Composable
    override fun ComposeContent() {
        MineScreen()
    }
}