// 定义包名，用于标识应用的命名空间
package com.venus.xiaohongshu

// 导入Android注解库，用于抑制特定的lint警告
import android.annotation.SuppressLint
// 导入Intent类，用于在Activity之间进行跳转
import android.content.Intent
// 导入Drawable类，用于处理图形资源
import android.graphics.drawable.Drawable
// 导入Bundle类，用于在Activity之间传递数据
import android.os.Bundle
// 导入SpannableString类，用于创建可格式化的字符串
import android.text.SpannableString
// 导入Spanned接口，用于处理富文本格式
import android.text.Spanned
// 导入ImageSpan类，用于在文本中嵌入图片
import android.text.style.ImageSpan
// 导入MotionEvent类，用于处理触摸事件
import android.view.MotionEvent
// 导入View类，Android UI的基础类
import android.view.View
// 导入enableEdgeToEdge扩展函数，用于启用边到边显示
import androidx.activity.enableEdgeToEdge
// 导入AppCompatActivity类，提供向后兼容的Activity基类
import androidx.appcompat.app.AppCompatActivity
// 导入Navigation类，用于处理导航组件
import androidx.navigation.Navigation
// 导入AppBarConfiguration类，用于配置应用栏
import androidx.navigation.ui.AppBarConfiguration
// 导入NavigationUI类，用于设置导航UI组件
import androidx.navigation.ui.NavigationUI
// 导入数据绑定类，用于访问布局中的视图
import com.venus.xiaohongshu.databinding.ActivityMainBinding
// 导入发布Activity类，用于处理内容发布功能
import com.venus.xiaohongshu.ui.publish.PublishActivity

/**
 * 主Activity类，作为应用的入口点和主要导航容器
 * 负责设置底部导航栏、处理导航逻辑以及发布功能的入口
 *
 * @author: poboll
 * @date: 2024/05/29
 */
class MainActivity : AppCompatActivity() {
    
    // 声明数据绑定对象，用于访问布局中的视图组件
    // 使用lateinit关键字表示稍后初始化
    lateinit var binding: ActivityMainBinding
    
    // 使用注解抑制"ClickableViewAccessibility"警告
    // 因为我们需要自定义触摸事件处理
    @SuppressLint("ClickableViewAccessibility")
    // 重写onCreate方法，Activity创建时调用
    override fun onCreate(savedInstanceState: Bundle?) {
        // 调用父类的onCreate方法，确保正常的Activity初始化流程
        super.onCreate(savedInstanceState)
        // 启用边到边显示模式，让内容可以延伸到状态栏和导航栏区域
        enableEdgeToEdge()
        // 通过数据绑定初始化布局，获取布局填充器并创建绑定对象
        binding = ActivityMainBinding.inflate(layoutInflater)
        // 设置Activity的内容视图为绑定对象的根视图
        setContentView(binding.root)
        
        // 查找并获取导航控制器，用于管理Fragment之间的导航
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main)
        // 将底部导航视图与导航控制器关联，实现自动导航功能
        NavigationUI.setupWithNavController(binding.navView, navController)
        // 获取底部导航视图的菜单对象，用于自定义菜单项
        val menu = binding.navView.menu
        // 遍历菜单中的所有项目，对发布按钮进行特殊处理
        for (i in 0 until menu.size()) {
            // 获取当前索引位置的菜单项
            val item = menu.getItem(i)
            // 检查当前菜单项是否为发布按钮
            if (item.itemId == R.id.navigation_publish) {
                // 如果菜单项有图标，则进行自定义处理
                item.icon?.let {
                    // 设置图标的边界大小为128x128像素
                    it.setBounds(0, 0, 128, 128)
                    // 创建一个包含空格的可格式化字符串
                    val spannableString = SpannableString(" ")
                    // 创建图片跨度对象，用于在文本中嵌入图标
                    val imageSpan = ImageSpan(it, ImageSpan.ALIGN_BASELINE)
                    // 将图片跨度应用到字符串的第一个字符位置
                    spannableString.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    // 移除原有的图标
                    item.setIcon(null)
                    // 将包含图片的字符串设置为菜单项的标题
                    item.setTitle(spannableString)
                }
            }
        }
        // 为发布按钮设置自定义触摸监听器，处理点击事件
        binding.navView.setItemOnTouchListener(R.id.navigation_publish
        // 定义触摸事件处理的lambda表达式，接收视图和触摸事件参数
        ) { _, event ->
            // 根据触摸事件的动作类型进行不同的处理
            when (event?.action) {
                // 当用户抬起手指时触发（完成点击操作）
                MotionEvent.ACTION_UP -> {
                    // 创建跳转到发布Activity的Intent对象，指定目标Activity类
                    val intent = Intent(baseContext, PublishActivity::class.java)
                    // 启动发布Activity，进入内容发布界面
                    startActivity(intent)
                }
            }
            // 返回true表示触摸事件已被成功处理，不再传递给其他监听器
            true
        }
    }
}