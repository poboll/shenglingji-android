package com.caiths.shenglingji.ui.mine.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caiths.shenglingji.ui.mine.model.User
import com.caiths.shenglingji.ui.mine.viewmodel.ProfileViewModel

/**
 * Description: 个人信息编辑界面
 *
 * @author: venus
 * @date: 2024/11/30
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val user = viewModel.currentUser.value
    
    var nickname by remember { mutableStateOf(user?.nickname ?: "") }
    var bio by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    
    // 监听更新结果
    LaunchedEffect(key1 = viewModel.updateResult) {
        viewModel.updateResult.collect { result ->
            isLoading = false
            if (result != null) {
                if (result.success) {
                    successMessage = "个人信息更新成功"
                    // 3秒后自动返回
                    kotlinx.coroutines.delay(2000)
                    onNavigateBack()
                } else {
                    errorMessage = result.message ?: "更新失败，请稍后再试"
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("编辑个人资料") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(
                            onClick = {
                                errorMessage = null
                                successMessage = null
                                isLoading = true
                                viewModel.updateProfile(
                                    nickname = nickname.takeIf { it.isNotBlank() },
                                )
                            }
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "保存")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                // 标题
                Text(
                    text = "基本信息",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 昵称输入框
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("昵称") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 个人简介输入框
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("个人简介 (暂不可用)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = false
                )
                
                // 错误信息
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
                
                // 成功信息
                if (successMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = successMessage!!,
                        color = Color.Green,
                        fontSize = 14.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 保存按钮
                Button(
                    onClick = {
                        errorMessage = null
                        successMessage = null
                        isLoading = true
                        viewModel.updateProfile(
                            nickname = nickname.takeIf { it.isNotBlank() },
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoading
                ) {
                    Text("保存修改")
                }
            }
        }
    }
} 