package com.caiths.shenglingji.ui.search

import androidx.compose.runtime.Composable
import com.caiths.shenglingji.base.BaseComposeActivity
import com.caiths.shenglingji.ui.search.composable.SearchScreen

/**
 * Description:
 *
 * @author: venus
 * @date: 2024/11/18
 */
class SearchActivity: BaseComposeActivity() {
    
    @Composable
    override fun ComposeContent() {
        SearchScreen()
    }
    
}