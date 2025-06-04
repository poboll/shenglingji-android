// 定义包名，标识基础类的命名空间
package com.venus.xiaohongshu.base

// 导入Bundle类，用于在Activity之间传递数据
import android.os.Bundle
// 导入setContent扩展函数，用于设置Compose内容
import androidx.activity.compose.setContent
// 导入enableEdgeToEdge扩展函数，用于启用边到边显示
import androidx.activity.enableEdgeToEdge
// 导入AppCompatActivity类，提供向后兼容的Activity基类
import androidx.appcompat.app.AppCompatActivity
// 导入Composable注解，用于标记可组合函数
import androidx.compose.runtime.Composable

/**
 * 基础Compose Activity抽象类
 * 为所有使用Jetpack Compose的Activity提供通用的初始化逻辑
 * 简化子类的实现，只需关注具体的UI内容
 *
 * @author: poboll
 * @date: 2024/05/30
 */
// 定义抽象的基础Compose Activity类，继承自AppCompatActivity
abstract class BaseComposeActivity: AppCompatActivity() {

    // 重写onCreate方法，Activity创建时调用
    override fun onCreate(savedInstanceState: Bundle?) {
        // 调用父类的onCreate方法，确保正常的Activity初始化流程
        super.onCreate(savedInstanceState)
        // 启用边到边显示模式，让内容可以延伸到状态栏和导航栏区域
        enableEdgeToEdge()
        // 设置Activity的内容为Compose UI
        setContent { 
            // 调用抽象方法，由子类实现具体的UI内容
            ComposeContent()
        }
    }

    // 抽象的可组合函数，由子类实现具体的UI内容
    @Composable
    abstract fun ComposeContent()
}