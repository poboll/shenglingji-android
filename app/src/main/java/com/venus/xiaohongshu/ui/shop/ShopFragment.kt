package com.venus.xiaohongshu.ui.shop

import androidx.compose.runtime.Composable
import com.venus.xiaohongshu.base.BaseComposeFragment
import com.venus.xiaohongshu.ui.shop.composable.ShopScreen

/**
 * Description:
 *
 * @author: poboll
 * @date: 2024/05/25
 */
class ShopFragment: BaseComposeFragment() {
    @Composable
    override fun ComposeContent() {
        ShopScreen()
    }
}