package com.caiths.shenglingji.ui.home.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.caiths.shenglingji.ui.home.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "首页")
    }
} 