package com.venus.xiaohongshu.activity.graphic.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.venus.xiaohongshu.activity.graphic.GraphicViewModel
import com.venus.xiaohongshu.ui.common.Divider
import com.venus.xiaohongshu.ui.home.follow.QuizViewModel

/**
 * Description:
 *
 * @author: poboll
 * @date: 2024/05/28
 */
@Composable
fun GraphicScreen() {
    val vm = viewModel<GraphicViewModel>()
    // 获取测验数据来检查是否显示勋章
    val quizViewModel = viewModel<QuizViewModel>()
    
    // 观察已识别的鸟类数量，如果达到50则显示勋章
    val birdCount = quizViewModel.birdIdentifiedCount.observeAsState().value ?: 0
    val showBirdExpertBadge = birdCount >= 50
    
    Scaffold(
        containerColor = Color.White
    ) { innerPadding -> 
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) { 
            GraphicTopBar(vm, showBadge = showBirdExpertBadge)

            GraphicBody(
                vm = vm,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 10.dp)
            )
            
            Divider()
            GraphicBottomBar(vm)
        }
    }
}