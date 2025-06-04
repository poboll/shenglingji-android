package com.venus.xiaohongshu.activity.graphic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.venus.xiaohongshu.activity.graphic.composable.GraphicScreen
import com.venus.xiaohongshu.base.BaseComposeActivity

/**
 * Description: 图文activity
 *
 * @author: poboll
 * @date: 2024/05/23
 */
const val KEY_ID = "key_id"
class GraphicActivity: BaseComposeActivity() {
    
    private val vm: GraphicViewModel by viewModels<GraphicViewModel>()
    private val TAG = "GraphicActivity"
    
    companion object {
        fun newInstance(context: Context, id: String) {
            val intent = Intent(context, GraphicActivity::class.java)
            intent.putExtra(KEY_ID, id);
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val postId = intent.getStringExtra(KEY_ID) ?: ""
        vm.id = postId
        
        Log.d(TAG, "正在加载帖子详情，ID: $postId")
        
        // 如果有ID，直接调用加载方法
        if (postId.isNotEmpty()) {
            vm.loadPostDetail(postId)
        } else {
            Log.e(TAG, "没有提供有效的帖子ID")
            // 可以显示错误信息或结束活动
            // finish()
        }
    }
    
    @Composable
    override fun ComposeContent() {
        GraphicScreen()
    }
}