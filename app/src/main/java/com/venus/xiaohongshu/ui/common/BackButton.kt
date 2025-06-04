package com.venus.xiaohongshu.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.venus.xiaohongshu.R

/**
 * Description:
 *
 * @author: poboll
 * @date: 2024/05/24
 */
@Composable
fun BackButton(
    onClick: () -> Unit
) {
    AsyncImage(
        model = R.drawable.icon_return,
        contentDescription = null,
        modifier = Modifier.size(26.dp)
            .clickable { 
                onClick()
            }
    )
}