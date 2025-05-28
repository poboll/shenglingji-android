package com.caiths.shenglingji.ui.mine

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.caiths.shenglingji.R
import com.caiths.shenglingji.base.BaseComposeFragment
import com.caiths.shenglingji.ui.mine.composable.LoginScreen
import com.caiths.shenglingji.ui.mine.composable.MineScreen
import com.caiths.shenglingji.ui.mine.viewmodel.AuthViewModel

/**
 * Description: 个人中心Fragment
 *
 * @author: venus
 * @date: 2024/11/15
 */
class MineFragment: BaseComposeFragment() {
    
    private val authViewModel: AuthViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 检查是否需要登录
        checkLoginStatus()
    }
    
    private fun checkLoginStatus() {
        // 如果未登录，导航到登录页面
        if (!authViewModel.isLoggedIn()) {
            findNavController().navigate(R.id.action_mine_to_login)
        }
    }
    
    @Composable
    override fun ComposeContent() {
        MineScreen()
    }
}