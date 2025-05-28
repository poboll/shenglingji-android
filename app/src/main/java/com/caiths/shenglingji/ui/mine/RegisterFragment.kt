package com.caiths.shenglingji.ui.mine

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.caiths.shenglingji.R
import com.caiths.shenglingji.base.BaseComposeFragment
import com.caiths.shenglingji.ui.mine.composable.RegisterScreen
import com.caiths.shenglingji.ui.mine.viewmodel.AuthViewModel

/**
 * Description: 注册Fragment
 *
 * @author: venus
 * @date: 2024/11/30
 */
class RegisterFragment : BaseComposeFragment() {
    
    private val authViewModel: AuthViewModel by viewModels()
    
    @Composable
    override fun ComposeContent() {
        RegisterScreen(
            viewModel = authViewModel,
            onNavigateToLogin = {
                findNavController().navigate(R.id.action_register_to_login)
            },
            onRegisterSuccess = {
                findNavController().navigate(R.id.action_register_to_login)
            }
        )
    }
} 