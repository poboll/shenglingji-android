// 定义包名，标识网络工具类的命名空间
package com.venus.xiaohongshu.utils

// 导入Android Context类，用于访问系统服务
import android.content.Context
// 导入连接管理器，用于检查网络状态
import android.net.ConnectivityManager
// 导入网络能力类，用于检查网络类型
import android.net.NetworkCapabilities
// 导入Build类，用于检查Android版本
import android.os.Build
// 导入Log类，用于日志输出
import android.util.Log
// 导入协程调度器，用于指定协程运行的线程
import kotlinx.coroutines.Dispatchers
// 导入withContext函数，用于切换协程上下文
import kotlinx.coroutines.withContext
// 导入IO异常类，用于处理网络异常
import java.io.IOException
// 导入网络地址类，用于创建Socket连接
import java.net.InetSocketAddress
// 导入Socket类，用于网络连接测试
import java.net.Socket
// 导入URL类，用于处理网络地址
import java.net.URL

/**
 * 网络工具类
 * 提供网络相关的实用功能，包括URL转换、网络状态检查和连接测试
 * 使用单例模式，确保全局唯一实例
 */
object NetworkUtils {
    // 日志标签，用于标识日志来源
    private const val TAG = "NetworkUtils"
    
    /**
     * 将localhost URL转换为Android模拟器可访问的URL
     * Android模拟器无法直接访问localhost，需要使用特殊IP地址10.0.2.2
     * @param url 原始URL字符串，可能包含localhost
     * @return 转换后的URL字符串，如果输入为空则返回原值
     */
    fun convertLocalhostUrl(url: String?): String? {
        // 检查URL是否为空或null
        if (url.isNullOrEmpty()) return url
        
        // 如果URL包含localhost，则替换为模拟器可访问的IP地址
        return if (url.contains("localhost")) {
            url.replace("localhost", "10.0.2.2")
        } else {
            // 如果不包含localhost，直接返回原URL
            url
        }
    }

    /**
     * 检查设备是否连接到互联网
     * 兼容不同Android版本的网络检查方式
     * @param context 应用程序上下文，用于获取系统服务
     * @return true表示网络可用，false表示网络不可用
     */
    fun isNetworkAvailable(context: Context): Boolean {
        // 获取连接管理器系统服务
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        // Android 6.0（API 23）及以上版本使用新的网络检查方式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 获取当前活动的网络，如果没有则返回false
            val network = connectivityManager.activeNetwork ?: return false
            // 获取网络能力信息，如果没有则返回false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            
            // 检查网络传输类型，支持WiFi、移动数据或以太网
            return when {
                // 检查是否有WiFi连接
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                // 检查是否有移动数据连接
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                // 检查是否有以太网连接
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                // 其他情况返回false
                else -> false
            }
        } else {
            // Android 6.0以下版本使用已弃用的API
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            // 返回网络是否已连接
            return networkInfo.isConnected
        }
    }
    
    /**
     * 测试服务器连接可达性
     * 使用Socket连接测试指定主机和端口是否可访问
     * @param host 服务器主机地址
     * @param port 服务器端口号
     * @param timeout 连接超时时间（毫秒），默认5秒
     * @return true表示服务器可达，false表示不可达
     */
    suspend fun isServerReachable(host: String, port: Int, timeout: Int = 5000): Boolean {
        // 切换到IO线程执行网络操作
        return withContext(Dispatchers.IO) {
            try {
                // 记录开始检测的日志
                Log.d(TAG, "正在检测服务器连接: $host:$port")
                // 创建Socket实例
                val socket = Socket()
                // 尝试连接到指定的主机和端口，设置超时时间
                socket.connect(InetSocketAddress(host, port), timeout)
                // 连接成功后立即关闭Socket
                socket.close()
                // 记录连接成功的日志
                Log.d(TAG, "服务器可访问: $host:$port")
                // 返回true表示连接成功
                true
            } catch (e: IOException) {
                // 捕获IO异常，记录连接失败的日志
                Log.e(TAG, "服务器不可访问: $host:$port - ${e.message}")
                // 返回false表示连接失败
                false
            }
        }
    }
    
    /**
     * 测试API端点可访问性
     * 通过HTTP连接测试API端点是否正常响应
     * @param urlString API端点的完整URL字符串
     * @return true表示API端点可访问，false表示不可访问
     */
    suspend fun testApiEndpoint(urlString: String): Boolean {
        // 切换到IO线程执行网络操作
        return withContext(Dispatchers.IO) {
            try {
                // 记录开始测试的日志
                Log.d(TAG, "测试API端点: $urlString")
                // 创建URL对象
                val url = URL(urlString)
                // 打开HTTP连接
                val connection = url.openConnection()
                // 设置连接超时时间为5秒
                connection.connectTimeout = 5000
                // 设置读取超时时间为5秒
                connection.readTimeout = 5000
                // 建立连接
                connection.connect()
                // 获取输入流并立即关闭，验证连接是否成功
                connection.getInputStream().close()
                // 记录连接成功的日志
                Log.d(TAG, "API端点可访问: $urlString")
                // 返回true表示API端点可访问
                true
            } catch (e: IOException) {
                // 捕获IO异常，记录连接失败的日志
                Log.e(TAG, "API端点不可访问: $urlString - ${e.message}")
                // 返回false表示API端点不可访问
                false
            }
        }
    }
}