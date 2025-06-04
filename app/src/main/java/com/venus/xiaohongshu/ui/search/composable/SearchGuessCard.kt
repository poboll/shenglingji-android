package com.venus.xiaohongshu.ui.search.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.venus.xiaohongshu.R

/**
 * Description:
 *
 * @author: poboll
 * @date: 2024/05/31
 */
@Composable
fun SearchGuessCard() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ){
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "猜你想搜",
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            AsyncImage(
                model = R.drawable.icon_refresh,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.padding(horizontal = 8.dp)
                .width(1.dp).height(12.dp)
                .background(color = colorResource(R.color.theme_background_gray)))
            AsyncImage(
                model = R.drawable.icon_eye,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
        SearchGuessItem("珍稀鸟类观察", "多肉植物养护")
        SearchGuessItem("野生动物保护", "室内绿植推荐")
        SearchGuessItem("蝴蝶生态习性", "花卉品种识别")
    }
}

@Composable
fun SearchGuessItem(leftText: String, rightText: String) {
    Row(modifier = Modifier.padding(top = 16.dp)) {
        Text(
            text = leftText,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = rightText,
            modifier = Modifier.weight(1f)
        )
    }
}