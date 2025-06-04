// 定义包名，标识基础类的命名空间
package com.venus.xiaohongshu.base

// 导入Bundle类，用于保存和恢复Fragment状态
import android.os.Bundle
// 导入LayoutInflater类，用于将布局XML文件转换为View对象
import android.view.LayoutInflater
// 导入View类，Android UI的基础类
import android.view.View
// 导入ViewGroup类，用于容纳其他View的容器
import android.view.ViewGroup
// 导入Composable注解，用于标记可组合函数
import androidx.compose.runtime.Composable
// 导入ComposeView类，用于在传统View系统中嵌入Compose内容
import androidx.compose.ui.platform.ComposeView
// 导入Fragment类，Android的UI片段基类
import androidx.fragment.app.Fragment

/**
 * 基础Compose Fragment抽象类
 * 为所有使用Jetpack Compose的Fragment提供通用的初始化逻辑
 * 简化子类的实现，只需关注具体的UI内容
 *
 * @author: poboll
 * @date: 2024/05/24
 */
// 定义抽象的基础Compose Fragment类，继承自Fragment
abstract class BaseComposeFragment: Fragment() {

    // 重写onCreateView方法，创建Fragment的视图
    override fun onCreateView(
        // 布局填充器，用于将XML布局转换为View
        inflater: LayoutInflater,
        // 父容器，Fragment将被添加到此容器中
        container: ViewGroup?,
        // 保存的实例状态，用于恢复Fragment状态
        savedInstanceState: Bundle?
    ): View {
        // 返回一个ComposeView实例，用于承载Compose内容
        return ComposeView(requireContext()).apply { 
            // 设置Compose内容
            setContent {
                // 调用抽象方法，由子类实现具体的UI内容
                ComposeContent()
            }
        }
    }
    
    // 抽象的可组合函数，由子类实现具体的UI内容
    @Composable
    abstract fun ComposeContent()
    
}