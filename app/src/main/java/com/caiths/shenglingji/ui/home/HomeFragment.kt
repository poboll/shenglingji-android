package com.caiths.shenglingji.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.caiths.shenglingji.base.BaseComposeFragment
import com.caiths.shenglingji.ui.home.composable.HomeScreen

class HomeFragment : BaseComposeFragment() {
    
    private val viewModel: HomeViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadData()
    }
    
    @Composable
    override fun ComposeContent() {
        HomeScreen(viewModel)
    }
} 