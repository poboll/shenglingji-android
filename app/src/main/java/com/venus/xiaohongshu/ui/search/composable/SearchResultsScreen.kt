package com.venus.xiaohongshu.ui.search.composable

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.venus.xiaohongshu.R
import com.venus.xiaohongshu.activity.graphic.GraphicActivity
import com.venus.xiaohongshu.activity.video.VideoActivity
import com.venus.xiaohongshu.ui.home.bean.GraphicCardBean
import com.venus.xiaohongshu.ui.home.bean.GraphicCardType
import com.venus.xiaohongshu.ui.home.composable.Shimmer
import com.venus.xiaohongshu.ui.search.viewmodel.SearchViewModel

/**
 * 搜索结果筛选器组件
 */
@Composable
fun SearchFilters(
    query: String,
    currentType: Int? = null,
    currentSort: String = "relevance",
    currentMediaType: String? = null,
    onTypeChange: (Int?) -> Unit,
    onSortChange: (String) -> Unit,
    onMediaTypeChange: (String?) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // 结果数量标题
        Text(
            text = "「${query}」的搜索结果",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
        
        // 筛选选项行
        ScrollableTabRow(
            selectedTabIndex = when(currentType) {
                1 -> 1
                2 -> 2
                else -> 0
            },
            edgePadding = 8.dp,
            divider = {},
            indicator = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        ) {
            FilterChip(
                selected = currentType == null,
                onClick = { onTypeChange(null) },
                label = { Text("全部", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colorResource(id = R.color.theme_red),
                    selectedLabelColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(end = 4.dp).height(30.dp)
            )
            FilterChip(
                selected = currentType == 1,
                onClick = { onTypeChange(1) },
                label = { Text("植物", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colorResource(id = R.color.theme_red),
                    selectedLabelColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(end = 4.dp).height(30.dp)
            )
            FilterChip(
                selected = currentType == 2,
                onClick = { onTypeChange(2) },
                label = { Text("动物", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colorResource(id = R.color.theme_red),
                    selectedLabelColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(end = 4.dp).height(30.dp)
            )
        }
        
        // 排序选项行
        ScrollableTabRow(
            selectedTabIndex = when(currentSort) {
                "newest" -> 1
                "oldest" -> 2
                "popular" -> 3
                else -> 0
            },
            edgePadding = 8.dp,
            divider = {},
            indicator = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        ) {
            FilterChip(
                selected = currentSort == "relevance",
                onClick = { onSortChange("relevance") },
                label = { Text("最相关", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colorResource(id = R.color.theme_red),
                    selectedLabelColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(end = 4.dp).height(30.dp)
            )
            FilterChip(
                selected = currentSort == "newest",
                onClick = { onSortChange("newest") },
                label = { Text("最新", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colorResource(id = R.color.theme_red),
                    selectedLabelColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(end = 4.dp).height(30.dp)
            )
            FilterChip(
                selected = currentSort == "oldest",
                onClick = { onSortChange("oldest") },
                label = { Text("最早", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colorResource(id = R.color.theme_red),
                    selectedLabelColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(end = 4.dp).height(30.dp)
            )
            FilterChip(
                selected = currentSort == "popular",
                onClick = { onSortChange("popular") },
                label = { Text("最热门", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colorResource(id = R.color.theme_red),
                    selectedLabelColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(end = 4.dp).height(30.dp)
            )
        }
        
        // 媒体类型筛选行
        ScrollableTabRow(
            selectedTabIndex = when(currentMediaType) {
                "image" -> 1
                "video" -> 2
                else -> 0
            },
            edgePadding = 8.dp,
            divider = {},
            indicator = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        ) {
            FilterChip(
                selected = currentMediaType == null,
                onClick = { onMediaTypeChange(null) },
                label = { Text("全部", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colorResource(id = R.color.theme_red),
                    selectedLabelColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(end = 4.dp).height(30.dp)
            )
            FilterChip(
                selected = currentMediaType == "image",
                onClick = { onMediaTypeChange("image") },
                label = { Text("图片", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colorResource(id = R.color.theme_red),
                    selectedLabelColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(end = 4.dp).height(30.dp)
            )
            FilterChip(
                selected = currentMediaType == "video",
                onClick = { onMediaTypeChange("video") },
                label = { Text("视频", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colorResource(id = R.color.theme_red),
                    selectedLabelColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(end = 4.dp).height(30.dp)
            )
        }
    }
}

/**
 * 搜索结果屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
    searchViewModel: SearchViewModel,
    query: String
) {
    // 当前筛选条件
    var currentType by remember { mutableStateOf<Int?>(null) }
    var currentSort by remember { mutableStateOf("relevance") }
    var currentMediaType by remember { mutableStateOf<String?>(null) }
    
    val pullToRefreshState = rememberPullToRefreshState()
    val resultCards by searchViewModel.searchResultCards.observeAsState(initial = emptyList())
    val isLoading by searchViewModel.isLoading.observeAsState(initial = false)
    val hasMoreData by searchViewModel.hasMoreData.observeAsState(initial = true)
    val context = LocalContext.current
    
    LaunchedEffect(query) {
        if (query.isNotEmpty()) {
            searchViewModel.search(query)
        }
    }
    
    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing) {
            searchViewModel.search(
                query = query, 
                type = currentType, 
                sort = currentSort, 
                mediaType = currentMediaType,
                forceRefresh = true
            )
            pullToRefreshState.endRefresh()
        }
    }
    
    // 筛选条件变化时重新搜索
    LaunchedEffect(currentType, currentSort, currentMediaType) {
        if (query.isNotEmpty()) {
            searchViewModel.search(
                query = query,
                type = currentType,
                sort = currentSort,
                mediaType = currentMediaType
            )
        }
    }
    
    Box(modifier = Modifier
        .fillMaxSize()
        .background(colorResource(R.color.theme_background_gray))
    ) {
        if (isLoading && resultCards.isEmpty()) {
            Shimmer()
        } else {
            Box(modifier = Modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    content = {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            SearchFilters(
                                query = query,
                                currentType = currentType,
                                currentSort = currentSort,
                                currentMediaType = currentMediaType,
                                onTypeChange = { currentType = it },
                                onSortChange = { currentSort = it },
                                onMediaTypeChange = { currentMediaType = it }
                            )
                        }
                        
                        if (resultCards.isEmpty() && !isLoading) {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 64.dp)
                                ) {
                                    AsyncImage(
                                        model = R.drawable.icon_search,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(80.dp)
                                            .padding(bottom = 16.dp)
                                            .alpha(0.5f)
                                    )
                                    Text(
                                        text = "未找到相关结果",
                                        fontSize = 16.sp,
                                        color = colorResource(R.color.theme_text_gray)
                                    )
                                    Text(
                                        text = "试试其他关键词或筛选条件",
                                        fontSize = 14.sp,
                                        color = colorResource(R.color.theme_text_gray),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        } else {
                            items(resultCards) { card ->
                                SearchResultCard(card = card)
                            }
                            
                            if (isLoading && resultCards.isNotEmpty()) {
                                item(span = StaggeredGridItemSpan.FullLine) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            color = colorResource(id = R.color.theme_red),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                            
                            if (!hasMoreData && resultCards.isNotEmpty()) {
                                item(span = StaggeredGridItemSpan.FullLine) {
                                    Text(
                                        text = "已经到底啦 ~",
                                        fontSize = 14.sp,
                                        color = colorResource(R.color.theme_text_gray),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp)
                                    )
                                }
                            }
                        }
                    },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                )
                
                // 下拉刷新指示器
                PullToRefreshContainer(
                    state = pullToRefreshState, 
                    modifier = Modifier.align(Alignment.TopCenter),
                    containerColor = colorResource(R.color.theme_background_gray),
                    contentColor = colorResource(R.color.theme_red)
                )
            }
        }
    }
    
    // 监听滚动到底部加载更多
    if (!isLoading && hasMoreData && resultCards.isNotEmpty()) {
        LaunchedEffect(key1 = resultCards.size) {
            searchViewModel.loadMore()
        }
    }
}

@Composable
fun SearchResultCard(card: GraphicCardBean) {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .padding(4.dp)
            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable { 
                if (card.type == GraphicCardType.Graphic) {
                    GraphicActivity.newInstance(context, card.id)
                } else {
                    VideoActivity.newInstance(context, card.id)
                }
            }
    ) {
        Box(
            contentAlignment = Alignment.TopEnd
        ) {
            // 优先使用URL图片，如果没有则使用资源ID
            if (card.imageUrl != null) {
                AsyncImage(
                    model = card.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                AsyncImage(
                    model = card.image,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            
            if (card.type == GraphicCardType.Video) {
                AsyncImage(
                    model = R.drawable.icon_stop_play,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp)
                )
            }
        }

        Text(
            text = card.title,
            modifier = Modifier.padding(8.dp),
            maxLines = 2,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            fontSize = 14.sp
        )

        Row(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 优先使用URL头像，如果没有则使用资源ID
            if (card.user.userAvatar != null) {
                AsyncImage(
                    model = card.user.userAvatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                )
            } else {
                AsyncImage(
                    model = card.user.image,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                )
            }
            
            Text(
                text = card.user.name,
                fontSize = 10.sp,
                color = colorResource(R.color.theme_text_gray),
                modifier = Modifier.padding(start = 6.dp)
            )
            Spacer(
                modifier = Modifier.weight(1f)
            )
            AsyncImage(
                model = R.drawable.icon_favorite,
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp)
            )
            Text(
                text = card.likes.toString(),
                fontSize = 10.sp,
                color = colorResource(R.color.theme_text_gray),
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
} 