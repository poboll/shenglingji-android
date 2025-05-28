package com.caiths.shenglingji.ui.shop

import androidx.compose.runtime.Composable
import com.caiths.shenglingji.base.BaseComposeFragment
import com.caiths.shenglingji.ui.shop.composable.ShopScreen

/**
 * Description:
 *
 * @author: venus
 * @date: 2024/11/15
 */
class ShopFragment: BaseComposeFragment() {
    @Composable
    override fun ComposeContent() {
        ShopScreen()
    }
}