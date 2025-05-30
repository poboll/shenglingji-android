package com.venus.xiaohongshu.ui.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.venus.xiaohongshu.R
import com.venus.xiaohongshu.databinding.FragmentProfileBinding
import com.venus.xiaohongshu.ui.user.LoginActivity

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: UserViewModel by activityViewModels()
    
    // 定义ActivityResultLauncher用于处理编辑资料页面的结果
    private lateinit var editProfileLauncher: ActivityResultLauncher<Intent>
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化ActivityResultLauncher
        editProfileLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // 刷新用户数据
                refreshUserData()
            }
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        loadUserData()
        observeViewModel()
    }
    
    private fun setupViews() {
        // 编辑资料按钮
        binding.llEditProfile.setOnClickListener {
            // 使用新的ActivityResultLauncher API启动编辑资料页面
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            editProfileLauncher.launch(intent)
        }
        
        // 修改密码按钮
        binding.llChangePassword.setOnClickListener {
            // TODO: 跳转到修改密码页面
        }
        
        // 退出登录按钮
        binding.llLogout.setOnClickListener {
            showLogoutConfirmDialog()
        }
    }
    
    private fun loadUserData() {
        // 从本地获取用户数据
        val user = viewModel.getSavedUser()
        
        if (user != null) {
            // 设置用户信息
            binding.tvUsername.text = user.nickname ?: user.username
            binding.tvBio.text = user.bio ?: getString(R.string.bio)
            binding.tvEmail.text = user.email
            binding.tvPhone.text = user.phone ?: "--"
            binding.tvGender.text = when (user.gender) {
                "male" -> getString(R.string.male)
                "female" -> getString(R.string.female)
                "other" -> getString(R.string.other_gender)
                else -> "--"
            }
            
            // 加载头像
            if (!user.avatar.isNullOrEmpty()) {
                Glide.with(this)
                    .load(user.avatar)
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(binding.ivProfileImage)
            }
        }
        
        // 从服务器刷新用户数据
        refreshUserData()
    }
    
    private fun refreshUserData() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.fetchCurrentUser()
    }
    
    private fun observeViewModel() {
        viewModel.currentUser.observe(viewLifecycleOwner) { result ->
            binding.progressBar.visibility = View.GONE
            
            result.onSuccess { user ->
                // 更新UI
                binding.tvUsername.text = user.nickname ?: user.username
                binding.tvBio.text = user.bio ?: getString(R.string.bio)
                binding.tvEmail.text = user.email
                binding.tvPhone.text = user.phone ?: "--"
                binding.tvGender.text = when (user.gender) {
                    "male" -> getString(R.string.male)
                    "female" -> getString(R.string.female)
                    "other" -> getString(R.string.other_gender)
                    else -> "--"
                }
                
                // 加载头像
                if (!user.avatar.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(user.avatar)
                        .placeholder(R.drawable.default_avatar)
                        .error(R.drawable.default_avatar)
                        .into(binding.ivProfileImage)
                }
            }
        }
    }
    
    private fun showLogoutConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.logout)
            .setMessage(R.string.logout_confirm)
            .setPositiveButton(R.string.confirm) { _, _ ->
                logout()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    private fun logout() {
        viewModel.logout()
        
        // 跳转到登录页面
        startActivity(Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        requireActivity().finish()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        private const val REQUEST_EDIT_PROFILE = 100
    }
} 