package com.caiths.shenglingji.ui.mine

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.caiths.shenglingji.base.BaseComposeFragment
import com.caiths.shenglingji.ui.mine.composable.ProfileEditScreen
import com.caiths.shenglingji.ui.mine.viewmodel.ProfileViewModel

/**
 * Description: 个人资料编辑Fragment
 *
 * @author: venus
 * @date: 2024/11/30
 */
class ProfileEditFragment : BaseComposeFragment() {
    
    private val profileViewModel: ProfileViewModel by viewModels()
    
    @Composable
    override fun ComposeContent() {
        ProfileEditScreen(
            viewModel = profileViewModel,
            onNavigateBack = {
                findNavController().navigateUp()
            }
        )
    }
} 