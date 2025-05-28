package com.caiths.shenglingji.ui.mine.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.PrimaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.Divider as MaterialDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil3.compose.AsyncImage
import com.caiths.shenglingji.R
import com.caiths.shenglingji.ui.common.Divider
import com.caiths.shenglingji.ui.mine.viewmodel.MineViewModel
import kotlinx.coroutines.launch

/**
 * Description:
 *
 * @author: venus
 * @date: 2024/11/21
 */
@Composable
fun BottomScrollingContent(
    vm: MineViewModel,
    onLogout: () -> Unit = {}
) {
    val maxHeight = LocalConfiguration.current.screenHeightDp.dp
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    val titleList = listOf("笔记", "收藏", "赞过")
    val graphicCardList = vm.graphicCardList.observeAsState().value
    
    LaunchedEffect(Unit) {
        vm.load()
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 我的收藏
        SectionTitle(title = "我的收藏")
        
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                MenuItem(
                    icon = Icons.Outlined.Bookmark,
                    title = "收藏的动物",
                    onClick = { }
                )
                
                MaterialDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                MenuItem(
                    icon = Icons.Outlined.Collections,
                    title = "收藏的植物",
                    onClick = { }
                )
                
                MaterialDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                MenuItem(
                    icon = Icons.Filled.Favorite,
                    title = "我的点赞",
                    onClick = { }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 浏览历史
        SectionTitle(title = "浏览历史")
        
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            MenuItem(
                icon = Icons.Outlined.History,
                title = "最近浏览",
                modifier = Modifier.padding(16.dp),
                onClick = { }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 设置
        SectionTitle(title = "设置")
        
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                MenuItem(
                    icon = Icons.Default.Settings,
                    title = "设置",
                    onClick = { }
                )
                
                MaterialDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                MenuItem(
                    icon = Icons.Default.ExitToApp,
                    title = "退出登录",
                    onClick = onLogout
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 版本信息
        Text(
            text = "盛灵记 v1.0.0",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun MenuItem(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = title,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "前往",
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
    }
}