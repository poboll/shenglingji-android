package com.venus.xiaohongshu.ui.settings

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.venus.xiaohongshu.R
import com.venus.xiaohongshu.databinding.ActivitySettingsBinding
import com.venus.xiaohongshu.ui.user.UserViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val userViewModel: UserViewModel by viewModels()
    private val TAG = "SettingsActivity"
    
    // 权限请求
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            exportUserData()
        } else {
            Toast.makeText(this, "需要存储权限才能导出数据", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupViews()
        observeViewModel()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "设置"
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }
    
    private fun setupViews() {
        // 导出个人数据按钮
        binding.layoutExportData.setOnClickListener {
            exportUserData()
        }
        
        // 清除缓存按钮
        binding.layoutClearCache.setOnClickListener {
            clearCache()
        }
        
        // 查看日志按钮
        binding.layoutViewLogs.setOnClickListener {
            showLogDialog()
        }
        
        // 退出登录按钮
        binding.layoutLogout.setOnClickListener {
            showLogoutDialog()
        }
        
        // 退出应用按钮
        binding.layoutExit.setOnClickListener {
            showExitDialog()
        }
        
        // 关于应用按钮
        binding.layoutAbout.setOnClickListener {
            showAboutDialog()
        }
        
        // 隐私政策按钮
        binding.layoutPrivacyPolicy.setOnClickListener {
            showPrivacyPolicyDialog()
        }
    }
    
    private fun observeViewModel() {
        // 观察登录结果
        userViewModel.loginResult.observe(this) { result ->
            if (result.isFailure) {
                Toast.makeText(this, "操作失败: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun showExportDataDialog() {
        AlertDialog.Builder(this)
            .setTitle("导出个人数据")
            .setMessage("将导出您的个人信息（用户名、昵称、邮箱等）到本地文件。密码等敏感信息不会被导出。\n\n导出的文件将保存在下载目录中。")
            .setPositiveButton("确定") { _, _ ->
                checkPermissionAndExport()
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun checkPermissionAndExport() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                exportUserData()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }
    
    private fun exportUserData() {
        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE
                
                val user = userViewModel.getSavedUser()
                if (user == null) {
                    Toast.makeText(this@SettingsActivity, "未找到用户数据", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                    return@launch
                }
                
                // 创建JSON对象
                val userData = JSONObject().apply {
                    put("导出时间", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
                    put("用户ID", user.id ?: "")
                    put("用户名", user.username ?: "")
                    put("昵称", user.nickname ?: "")
                    put("邮箱", user.email ?: "")
                    put("手机号", user.phone ?: "")
                    put("个人简介", user.bio ?: "")
                    put("性别", user.gender ?: "")
                    put("头像URL", user.avatar ?: "")
                    put("注册时间", user.createdAt ?: "")
                    put("最后更新时间", user.createdAt ?: "")
                    put("备注", "此文件包含您在生灵集应用中的个人信息。为保护您的隐私，密码等敏感信息未包含在此文件中。")
                }
                
                // 创建文件
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }
                
                val fileName = "生灵集_个人数据_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.json"
                val file = File(downloadsDir, fileName)
                
                // 写入文件
                FileWriter(file).use { writer ->
                    writer.write(userData.toString(4)) // 格式化JSON，缩进4个空格
                }
                
                binding.progressBar.visibility = View.GONE
                
                AlertDialog.Builder(this@SettingsActivity)
                    .setTitle("导出成功")
                    .setMessage("个人数据已成功导出到：\n${file.absolutePath}")
                    .setPositiveButton("确定", null)
                    .show()
                
                Log.d(TAG, "用户数据导出成功: ${file.absolutePath}")
                
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Log.e(TAG, "导出用户数据失败", e)
                Toast.makeText(this@SettingsActivity, "导出失败: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun showClearCacheDialog() {
        AlertDialog.Builder(this)
            .setTitle("清除缓存")
            .setMessage("确定要清除应用缓存吗？这将删除临时文件和图片缓存。")
            .setPositiveButton("确定") { _, _ ->
                clearCache()
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun clearCache() {
        try {
            // 清除应用缓存目录
            val cacheDir = cacheDir
            if (cacheDir.exists()) {
                deleteRecursive(cacheDir)
            }
            
            // 清除外部缓存目录
            val externalCacheDir = externalCacheDir
            if (externalCacheDir?.exists() == true) {
                deleteRecursive(externalCacheDir)
            }
            
            Toast.makeText(this, "缓存清除成功", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "清除缓存失败", e)
            Toast.makeText(this, "清除缓存失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) {
            fileOrDirectory.listFiles()?.forEach { child ->
                deleteRecursive(child)
            }
        }
        fileOrDirectory.delete()
    }
    
    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle("关于生灵集")
            .setMessage("生灵集 v1.0.0\n\n一个专注于分享生活美好瞬间的社交平台。\n\n© 2024 生灵集团队")
            .setPositiveButton("确定", null)
            .show()
    }
    
    private fun showPrivacyPolicyDialog() {
        AlertDialog.Builder(this)
            .setTitle("隐私政策")
            .setMessage("我们重视您的隐私保护。\n\n• 我们只收集必要的用户信息\n• 您的个人数据将被安全存储\n• 我们不会向第三方分享您的个人信息\n• 您可以随时导出或删除您的数据\n\n如需了解详细的隐私政策，请访问我们的官方网站。")
            .setPositiveButton("确定", null)
            .show()
    }
    
    private fun showLogDialog() {
        val logInfo = StringBuilder()
        logInfo.append("应用版本: 1.0.0\n")
        logInfo.append("Android版本: ${android.os.Build.VERSION.RELEASE}\n")
        logInfo.append("设备型号: ${android.os.Build.MODEL}\n")
        logInfo.append("设备制造商: ${android.os.Build.MANUFACTURER}\n")
        logInfo.append("最后启动时间: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}\n")
        logInfo.append("用户状态: ${if (userViewModel.isLoggedIn()) "已登录" else "未登录"}\n")
        
        AlertDialog.Builder(this)
            .setTitle("日志信息")
            .setMessage(logInfo.toString())
            .setPositiveButton("确定", null)
            .setNegativeButton("复制") { _, _ ->
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("日志信息", logInfo.toString())
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, "日志信息已复制到剪贴板", Toast.LENGTH_SHORT).show()
            }
            .show()
    }
    
    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("注销确认")
            .setMessage("确定要注销当前账户吗？注销后需要重新登录。")
            .setPositiveButton("确定") { _, _ ->
                userViewModel.logout()
                Toast.makeText(this, "已注销", Toast.LENGTH_SHORT).show()
                // 跳转到登录页面
                val intent = Intent(this, com.venus.xiaohongshu.ui.user.LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setTitle("退出应用")
            .setMessage("确定要退出应用吗？")
            .setPositiveButton("确定") { _, _ ->
                finishAffinity() // 关闭所有Activity
                System.exit(0) // 完全退出应用
            }
            .setNegativeButton("取消", null)
            .show()
    }
}