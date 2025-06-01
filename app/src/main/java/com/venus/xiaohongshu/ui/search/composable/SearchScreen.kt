package com.venus.xiaohongshu.ui.search.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.venus.xiaohongshu.R
import com.venus.xiaohongshu.ui.common.Divider
import com.venus.xiaohongshu.ui.search.bean.HotspotBean
import com.venus.xiaohongshu.ui.search.viewmodel.SearchViewModel
import androidx.compose.foundation.clickable

/**
 * 搜索页面
 */
@Preview
@Composable
fun SearchScreen() {
    val vm = viewModel<SearchViewModel>()
    val hotspotList = vm.hotspotList.observeAsState().value
    val searchHistory = vm.searchHistory.observeAsState().value ?: emptyList()
    
    // 搜索状态
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        vm.load();
    }
    
    Scaffold(
        containerColor = Color.White
    ) { innerPadding -> 
        Column(
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding(),
                start = 16.dp,
                end = 16.dp
            )
        ) {
            // 搜索顶部栏
            SearchTopBar(
                initialQuery = searchQuery,
                onSearch = { query ->
                    searchQuery = query
                    isSearchActive = true
                },
                onBackClick = {
                    if (isSearchActive) {
                        // 返回搜索主页
                        isSearchActive = false
                        searchQuery = ""
                    }
                }
            )
            
            // 根据搜索状态显示不同内容
            if (isSearchActive) {
                // 显示搜索结果
                SearchResultsScreen(
                    searchViewModel = vm,
                    query = searchQuery
                )
            } else {
                // 显示搜索主页
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                        .padding(top = 16.dp)
                ) { 
                    // 历史记录
                    if (searchHistory.isNotEmpty()) {
                        item {
                            SearchHistoryCard(
                                historyList = searchHistory,
                                onClearAll = { vm.clearSearchHistory() },
                                onItemClick = { query ->
                                    searchQuery = query
                                    isSearchActive = true
                                },
                                onItemRemove = { query -> vm.removeHistoryItem(query) }
                            )
                        }
                    }
                    
                    // 猜你想搜
                    item {
                        SearchGuessCard()
                    }
                    
                    //热点title
                    item{
                        SearchHotTitleCard()
                    }

                    hotspotList?.let { 
                        itemsIndexed(items = hotspotList) { index, item ->
                            SearchHotspotItem(
                                index = index,
                                hotspotBean = item,
                                onClick = {
                                    searchQuery = item.title
                                    isSearchActive = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchHotspotItem(
    index: Int,
    hotspotBean: HotspotBean,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
    ) { 
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) { 
            Text(
                text = "${index + 1}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if(index == 0) colorResource(R.color.theme_red) 
                else if(index == 1) Color(0xFFFF8C00)
                else if(index == 2) Color(0xFFFF8C00)
                else colorResource(R.color.theme_text_gray),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                text = hotspotBean.title,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${"%.1f".format(hotspotBean.heat)}万",
                color = colorResource(R.color.theme_text_gray)
            )
        }
        Divider()
    }
}


