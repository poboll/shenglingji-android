package com.venus.xiaohongshu.ui.home.follow

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.venus.xiaohongshu.R
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay

/**
 * 测验页面 - 替代原有的关注页面
 * 实现鸟类识别测验获取积分功能
 */
@Composable
fun QuizPage() {
    val vm = viewModel<QuizViewModel>()
    val currentQuiz = vm.currentQuiz.observeAsState().value
    val userPoints = vm.userPoints.observeAsState().value ?: 0
    val birdProgress = vm.birdIdentifiedCount.observeAsState().value ?: 0
    
    // 移除自动加载测验，只有用户主动点击开始按钮时才加载
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 当前没有测验题目时显示简介
        if (currentQuiz == null) {
            QuizIntroCard(
                userPoints = userPoints,
                birdProgress = birdProgress,
                onStartQuiz = { vm.loadQuiz() }
            )
        } else {
            // 显示当前测验题目
            DraggableQuizCard(
                quiz = currentQuiz,
                viewModel = vm,
                onSwipeResult = { result ->
                    when (result) {
                        SwipeResult.ACCEPTED -> vm.submitAnswer(currentQuiz.correctOptionId)
                        SwipeResult.REJECTED -> vm.skipQuiz()
                    }
                }
            )
        }
        
        // 顶部显示积分和进度
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val (pointsRef, badgeRef, progressRef) = remember { createRefs() }
            
            // 积分显示
            Row(
                modifier = Modifier
                    .constrainAs(pointsRef) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
                    .background(
                        color = Color(0x30000000),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = R.drawable.icon_fire,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "$userPoints 积分",
                    color = colorResource(R.color.theme_background_gray),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            // 勋章进度
            Box(
                modifier = Modifier
                    .constrainAs(progressRef) {
                        top.linkTo(pointsRef.bottom, margin = 8.dp)
                        end.linkTo(parent.end)
                    }
                    .background(
                        color = Color(0x30000000),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .width(200.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "观鸟达人进度",
                            color = colorResource(R.color.theme_background_gray),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "$birdProgress/50",
                            color = colorResource(R.color.theme_background_gray),
                            fontSize = 12.sp,
                        )
                    }
                    
                    // 进度条
                    LinearProgressIndicator(
                        progress = birdProgress.toFloat() / 50f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = colorResource(R.color.theme_red),
                        trackColor = Color.Gray.copy(alpha = 0.3f)
                    )
                }
            }
            
            // 解锁勋章动画
            if (birdProgress >= 50) {
                BadgeUnlockEffect(
                    modifier = Modifier
                        .constrainAs(badgeRef) {
                            top.linkTo(progressRef.bottom, margin = 16.dp)
                            end.linkTo(parent.end)
                        }
                        .size(80.dp)
                )
            }
        }
    }
}

@Composable
fun QuizIntroCard(
    userPoints: Int,
    birdProgress: Int,
    onStartQuiz: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = R.drawable.icon_love,
            contentDescription = null,
            modifier = Modifier
                .padding(top = 16.dp)
                .size(68.dp)
                .background(color = Color(0xFFE5E5E5), shape = CircleShape)
                .padding(8.dp)
        )
        
        Text(
            text = "测验挑战",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
        
        Text(
            text = "参加鸟类识别测验，获取积分和勋章！\n每识别一种鸟类获得5积分，识别50种解锁观鸟达人勋章。",
            fontSize = 14.sp,
            color = colorResource(id = R.color.theme_text_gray),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "$userPoints",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.theme_red)
                )
                Text(
                    text = "我的积分",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "$birdProgress/50",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.theme_red)
                )
                Text(
                    text = "识别进度",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
        
        if (birdProgress >= 50) {
            Box(
                modifier = Modifier.padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = R.drawable.icon_fire,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = Color(0xFFFFD700).copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                        .padding(16.dp)
                )
                Text(
                    text = "观鸟达人",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700),
                    modifier = Modifier
                        .padding(top = 100.dp)
                        .background(
                            color = Color(0xFFFFD700).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
        
        Button(
            onClick = onStartQuiz,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.theme_red)
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = if (birdProgress >= 50) "继续挑战" else "开始测验",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun DraggableQuizCard(
    quiz: BirdQuiz,
    viewModel: QuizViewModel,
    onSwipeResult: (SwipeResult) -> Unit
) {
    var showOptions by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isAnswerCorrect by remember { mutableStateOf<Boolean?>(null) }
    var showAnswerFeedback by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(quiz) {
        // 重置状态
        showOptions = false
        selectedOption = null
        isAnswerCorrect = null
        showAnswerFeedback = false
        // 延迟显示选项
        delay(1000)
        showOptions = true
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 顶部提示
        Text(
            text = "鸟类识别挑战",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.theme_red),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // 鸟类图片
        AsyncImage(
            model = quiz.imageUrl,
            contentDescription = "鸟类图片",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(8.dp)),
            onState = { state ->
                when (state) {
                    is AsyncImagePainter.State.Loading -> {
                        android.util.Log.d("QuizPage", "Coil Image Loading: ${quiz.imageUrl}")
                    }
                    is AsyncImagePainter.State.Success -> {
                        android.util.Log.d("QuizPage", "Coil Image Success: ${quiz.imageUrl}")
                        viewModel.onImageLoadSuccess()
                    }
                    is AsyncImagePainter.State.Error -> {
                        android.util.Log.e("QuizPage", "Coil Image Error: ${quiz.imageUrl}, Error: ${state.result.throwable}", state.result.throwable)
                        viewModel.onImageLoadFailure()
                    }
                    is AsyncImagePainter.State.Empty -> {
                        android.util.Log.d("QuizPage", "Coil Image Empty: ${quiz.imageUrl}")
                    }
                }
            }
        )
        
        // 题目描述
        Text(
            text = "这是什么鸟？",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        // 选项区域
        AnimatedVisibility(
            visible = showOptions,
            enter = fadeIn(animationSpec = tween(300)) + 
                    expandVertically(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300)) + 
                   shrinkVertically(animationSpec = tween(300))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                quiz.options.forEach { option ->
                    OptionItem(
                        option = option,
                        isSelected = selectedOption == option.id,
                        isCorrect = isAnswerCorrect != null && option.id == quiz.correctOptionId,
                        showCorrectState = showAnswerFeedback,
                        onSelect = { 
                            if (selectedOption == null) { // 防止重复点击
                                selectedOption = option.id
                                isAnswerCorrect = option.id == quiz.correctOptionId
                                showAnswerFeedback = true
                                
                                // 延迟一下，让用户看到选择效果和积分反馈
                                coroutineScope.launch {
                                    delay(2000) // 增加到2秒，让用户有足够时间查看反馈
                                    if (option.id == quiz.correctOptionId) {
                                        onSwipeResult(SwipeResult.ACCEPTED)
                                    } else {
                                        onSwipeResult(SwipeResult.REJECTED)
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
        
        // 答对弹窗提示
        AnimatedVisibility(
            visible = isAnswerCorrect == true && showAnswerFeedback,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .background(
                        color = Color(0xFF4CAF50).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "回答正确!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    Text(
                        text = "获得 +5 积分",
                        fontSize = 16.sp,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = quiz.description,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
        
        // 答错提示
        AnimatedVisibility(
            visible = isAnswerCorrect == false && showAnswerFeedback,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .background(
                        color = colorResource(R.color.theme_red).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "回答错误",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.theme_red)
                    )
                    Text(
                        text = "正确答案: ${quiz.options.find { it.id == quiz.correctOptionId }?.text}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
        
        // 跳过按钮
        Button(
            onClick = { onSwipeResult(SwipeResult.REJECTED) },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Gray
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(
                text = "跳过",
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun OptionItem(
    option: QuizOption,
    isSelected: Boolean,
    isCorrect: Boolean = false,
    showCorrectState: Boolean = false,
    onSelect: () -> Unit
) {
    val backgroundColor = when {
        showCorrectState && isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.2f) // 正确答案显示绿色
        isSelected && !showCorrectState -> Color.Gray.copy(alpha = 0.2f) // 选中但未确认结果
        isSelected && showCorrectState && !isCorrect -> colorResource(R.color.theme_red).copy(alpha = 0.2f) // 选中但错误
        else -> Color.Gray.copy(alpha = 0.1f) // 默认未选中
    }
    
    val borderColor = when {
        showCorrectState && isCorrect -> Color(0xFF4CAF50) // 正确答案显示绿色边框
        isSelected && !showCorrectState -> Color.Gray // 选中但未确认结果
        isSelected && showCorrectState && !isCorrect -> colorResource(R.color.theme_red) // 选中但错误
        else -> Color.Transparent // 默认无边框
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = !showCorrectState) { onSelect() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = option.text,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun BadgeUnlockEffect(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )
    
    val scale by animateFloatAsState(
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // 发光效果
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = Color(0xFFFFD700).copy(alpha = 0.3f),
                    shape = CircleShape
                )
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        )
        
        // 勋章图标
        AsyncImage(
            model = R.drawable.icon_fire,
            contentDescription = "观鸟达人勋章",
            modifier = Modifier
                .size(50.dp)
                .graphicsLayer {
                    rotationZ = rotation
                }
        )
    }
}