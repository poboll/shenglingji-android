package com.venus.xiaohongshu.ui.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.venus.xiaohongshu.MainActivity
import com.venus.xiaohongshu.R
import com.venus.xiaohongshu.data.api.RetrofitModule
import com.venus.xiaohongshu.databinding.ActivityLoginBinding
import com.venus.xiaohongshu.utils.NetworkUtils
import com.venus.xiaohongshu.utils.isValidEmail
import kotlinx.coroutines.launch
import java.net.URL

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: UserViewModel by viewModels()
    private val TAG = "LoginActivity"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 检查网络连接
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "网络连接不可用，请检查网络设置", Toast.LENGTH_LONG).show()
        } else {
            // 测试API连接
            lifecycleScope.launch {
                testApiConnection()
            }
        }
        
        // 检查用户是否已经登录
        if (viewModel.isLoggedIn()) {
            navigateToMain()
            return
        }
        
        setupViews()
        observeViewModel()
    }
    
    private fun setupViews() {
        // 登录按钮点击事件
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            
            if (validateInputs(username, password)) {
                login(username, password)
            }
        }
        
        // 注册页面跳转
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
    
    private suspend fun testApiConnection() {
        try {
            // 使用RetrofitModule的BASE_URL
            val url = URL(RetrofitModule.BASE_URL)
            val host = url.host
            val port = if (url.port == -1) url.defaultPort else url.port
            
            Log.d(TAG, "测试API连接: $host:$port")
            val isReachable = NetworkUtils.isServerReachable(host, port)
            if (!isReachable) {
                runOnUiThread {
                    Toast.makeText(this, "无法连接到后端服务器 $host:$port，请确保服务器已启动且地址正确", Toast.LENGTH_LONG).show()
                    Log.e(TAG, "无法连接到后端服务器 $host:$port")
                }
            } else {
                Log.d(TAG, "成功连接到后端服务器 $host:$port")
            }
        } catch (e: Exception) {
            Log.e(TAG, "测试API连接时出错: ${e.message}", e)
            runOnUiThread {
                Toast.makeText(this, "测试API连接时出错: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { result ->
            binding.progressBar.visibility = View.GONE
            
            result.onSuccess {
                Toast.makeText(this, R.string.login_successful, Toast.LENGTH_SHORT).show()
                navigateToMain()
            }.onFailure { e ->
                Log.e(TAG, "登录失败: ${e.message}", e)
                val errorMsg = e.message ?: getString(R.string.login_failed)
                Toast.makeText(this, "登录失败: $errorMsg", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun validateInputs(username: String, password: String): Boolean {
        // 检查用户名
        if (username.isEmpty()) {
            binding.tilUsername.error = getString(R.string.field_required)
            return false
        } else {
            binding.tilUsername.error = null
        }
        
        // 检查密码
        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.field_required)
            return false
        } else if (password.length < 6) {
            binding.tilPassword.error = getString(R.string.min_password_length)
            return false
        } else {
            binding.tilPassword.error = null
        }
        
        return true
    }
    
    private fun login(username: String, password: String) {
        // 先检查网络连接
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "网络连接不可用，请检查网络设置", Toast.LENGTH_LONG).show()
            return
        }
        
        binding.progressBar.visibility = View.VISIBLE
        Log.d(TAG, "尝试登录: 用户名: $username")
        viewModel.login(username, password)
    }
    
    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
} 