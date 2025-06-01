package com.venus.xiaohongshu.ui.search.composable

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.venus.xiaohongshu.R

/**
 * 搜索历史记录卡片
 * 
 * @param historyList 历史记录列表
 * @param onClearAll 清空全部历史记录的回调
 * @param onItemClick 点击历史记录项的回调
 * @param onItemRemove 删除单个历史记录的回调
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchHistoryCard(
    historyList: List<String> = emptyList(),
    onClearAll: () -> Unit = {},
    onItemClick: (String) -> Unit = {},
    onItemRemove: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "历史记录",
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            AsyncImage(
                model = R.drawable.icon_lajitong,
                contentDescription = "清空历史记录",
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onClearAll() }
            )
        }

        if (historyList.isEmpty()) {
            Text(
                text = "暂无搜索历史",
                color = colorResource(R.color.theme_text_gray),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 12.dp)
            )
        } else {
            FlowRow(modifier = Modifier.padding(top = 6.dp)) {
                historyList.forEach { item ->
                    HistoryItem(
                        text = item,
                        onItemClick = { onItemClick(item) },
                        onItemRemove = { onItemRemove(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryItem(
    text: String,
    onItemClick: () -> Unit = {},
    onItemRemove: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 6.dp, end = 10.dp)
            .height(32.dp)
            .border(
                width = 1.dp,
                color = colorResource(R.color.theme_background_gray),
                RoundedCornerShape(50)
            )
            .clickable(onClick = onItemClick)
            .padding(start = 12.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
    ) {
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(end = 4.dp)
        )
        
        AsyncImage(
            model = R.drawable.icon_close,
            contentDescription = "删除历史记录",
            modifier = Modifier
                .size(16.dp)
                .clickable(onClick = onItemRemove)
        )
    }
}