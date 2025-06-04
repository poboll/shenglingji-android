package com.venus.xiaohongshu.ui.search

import androidx.compose.runtime.Composable
import com.venus.xiaohongshu.base.BaseComposeActivity
import com.venus.xiaohongshu.ui.search.composable.SearchScreen

/**
 * Description:
 *
 * @author: poboll
 * @date: 2024/06/01
 */
class SearchActivity: BaseComposeActivity() {
    
    @Composable
    override fun ComposeContent() {
        SearchScreen()
    }
    
}