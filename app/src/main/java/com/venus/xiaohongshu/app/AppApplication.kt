// 定义包名，标识应用程序相关类的命名空间
package com.venus.xiaohongshu.app

// 导入Android Application类，作为应用程序的基类
import android.app.Application
// 导入Context类，用于访问应用程序环境信息
import android.content.Context

/**
 * 自定义Application类，用于管理应用程序的全局状态和初始化
 * 提供全局的应用程序上下文访问
 *
 * @author: poboll
 * @date: 2024/05/26
 */
// 定义全局常量TAG，用于日志标记和调试
const val TAG = "venus-xiaohongshu"
// 自定义Application类，继承自Android的Application基类
class AppApplication: Application() {
    
    // 伴生对象，用于提供静态访问方式
    companion object {
        // 声明全局应用程序上下文变量，使用lateinit延迟初始化
        lateinit var appContext: Context
            // 设置私有setter，防止外部修改
            private  set
    }

    // 重写onCreate方法，应用程序创建时调用
    override fun onCreate() {
        // 调用父类的onCreate方法，确保正常的Application初始化
        super.onCreate()
        // 将应用程序上下文赋值给全局变量，供其他类使用
        appContext = applicationContext
    }
}