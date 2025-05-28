package com.caiths.shenglingji.ui.mine

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.caiths.shenglingji.R
import com.caiths.shenglingji.base.BaseComposeFragment
import com.caiths.shenglingji.ui.mine.composable.LoginScreen
import com.caiths.shenglingji.ui.mine.viewmodel.AuthViewModel

/**
 * Description: 登录Fragment
 *
 * @author: venus
 * @date: 2024/11/30
 */
class LoginFragment : BaseComposeFragment() {
    
    private val authViewModel: AuthViewModel by viewModels()
    
    @Composable
    override fun ComposeContent() {
        LoginScreen(
            viewModel = authViewModel,
            onNavigateToRegister = {
                findNavController().navigate(R.id.action_login_to_register)
            },
            onLoginSuccess = {
                findNavController().navigate(R.id.action_login_to_mine)
            }
        )
    }
} 