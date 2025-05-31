package com.venus.xiaohongshu.activity.video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.gyf.immersionbar.ImmersionBar
import com.venus.xiaohongshu.activity.graphic.KEY_ID
import com.venus.xiaohongshu.activity.video.composable.VideoScreen
import com.venus.xiaohongshu.base.BaseComposeActivity

/**
 * Description:
 *
 * @author: venus
 * @date: 2024/11/22
 */
class VideoActivity: BaseComposeActivity() {
    private val vm: VideoViewModel by viewModels<VideoViewModel>()
    private val TAG = "VideoActivity"

    companion object {
        fun newInstance(context: Context, id: String) {
            try {
                val intent = Intent(context, VideoActivity::class.java)
                intent.putExtra(KEY_ID, id)
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e("VideoActivity", "启动视频页面失败: ${e.message}", e)
                Toast.makeText(context, "无法打开视频: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            // 设置全屏沉浸式状态栏
            ImmersionBar.with(this)
                .statusBarDarkFont(false)  // 状态栏文字为白色
                .fullScreen(true)          // 全屏显示
                .init()
            
            // 获取帖子ID
            val postId = intent.getStringExtra(KEY_ID) ?: ""
            if (postId.isEmpty()) {
                Log.e(TAG, "未提供帖子ID，无法显示视频")
                Toast.makeText(this, "无法打开视频：未提供视频ID", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
            
            Log.d(TAG, "加载视频帖子，ID: $postId")
            vm.id = postId
            vm.init()
        } catch (e: Exception) {
            Log.e(TAG, "初始化视频页面失败: ${e.message}", e)
            Toast.makeText(this, "初始化失败: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    @Composable
    override fun ComposeContent() {
        VideoScreen()
    }
    
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "VideoActivity 已恢复，当前有 ${vm.videoList.size} 个视频，加载状态: ${vm.isLoading}")
        
        // 如果视频列表为空且没有在加载且已经初始化过，可能是加载失败，重新尝试加载
        if (vm.videoList.isEmpty() && !vm.isLoading && vm.id.isNotEmpty()) {
            Log.d(TAG, "视频列表为空且未在加载，尝试重新加载")
            vm.init()
        }
    }
    
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "VideoActivity 已暂停")
    }
}