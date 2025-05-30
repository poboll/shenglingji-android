package com.venus.xiaohongshu.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.venus.xiaohongshu.MainActivity
import com.venus.xiaohongshu.R
import com.venus.xiaohongshu.databinding.ActivityRegisterBinding
import com.venus.xiaohongshu.utils.isValidEmail

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: UserViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViews()
        observeViewModel()
    }
    
    private fun setupViews() {
        // 返回按钮
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        
        // 注册按钮
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val nickname = binding.etNickname.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            
            if (validateInputs(username, email, password, confirmPassword)) {
                register(username, email, password, nickname.ifEmpty { null }, phone.ifEmpty { null })
            }
        }
    }
    
    private fun observeViewModel() {
        viewModel.registerResult.observe(this) { result ->
            binding.progressBar.visibility = View.GONE
            
            result.onSuccess {
                Toast.makeText(this, R.string.register_successful, Toast.LENGTH_SHORT).show()
                navigateToMain()
            }.onFailure { e ->
                Toast.makeText(this, e.message ?: getString(R.string.register_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun validateInputs(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        var isValid = true
        
        // 检查用户名
        if (username.isEmpty()) {
            binding.tilUsername.error = getString(R.string.field_required)
            isValid = false
        } else if (username.length < 3) {
            binding.tilUsername.error = "用户名长度至少为3个字符"
            isValid = false
        } else {
            binding.tilUsername.error = null
        }
        
        // 检查邮箱
        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.field_required)
            isValid = false
        } else if (!isValidEmail(email)) {
            binding.tilEmail.error = getString(R.string.invalid_email)
            isValid = false
        } else {
            binding.tilEmail.error = null
        }
        
        // 检查密码
        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.field_required)
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = getString(R.string.min_password_length)
            isValid = false
        } else {
            binding.tilPassword.error = null
        }
        
        // 检查确认密码
        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.error = getString(R.string.field_required)
            isValid = false
        } else if (password != confirmPassword) {
            binding.tilConfirmPassword.error = getString(R.string.passwords_not_match)
            isValid = false
        } else {
            binding.tilConfirmPassword.error = null
        }
        
        return isValid
    }
    
    private fun register(
        username: String,
        email: String,
        password: String,
        nickname: String?,
        phone: String?
    ) {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.register(username, email, password, nickname, phone)
    }
    
    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
} 