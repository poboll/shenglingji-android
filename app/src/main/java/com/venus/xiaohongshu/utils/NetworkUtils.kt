package com.venus.xiaohongshu.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL

object NetworkUtils {
    private const val TAG = "NetworkUtils"
    
    /**
     * 将localhost URL转换为Android模拟器可访问的URL
     * 将localhost替换为10.0.2.2，以便Android模拟器可以访问宿主机服务
     */
    fun convertLocalhostUrl(url: String?): String? {
        if (url.isNullOrEmpty()) return url
        
        return if (url.contains("localhost")) {
            url.replace("localhost", "10.0.2.2")
        } else {
            url
        }
    }

    /**
     * 检查设备是否连接到互联网
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
    
    /**
     * 测试服务器连接
     */
    suspend fun isServerReachable(host: String, port: Int, timeout: Int = 5000): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "正在检测服务器连接: $host:$port")
                val socket = Socket()
                socket.connect(InetSocketAddress(host, port), timeout)
                socket.close()
                Log.d(TAG, "服务器可访问: $host:$port")
                true
            } catch (e: IOException) {
                Log.e(TAG, "服务器不可访问: $host:$port - ${e.message}")
                false
            }
        }
    }
    
    /**
     * 测试API端点
     */
    suspend fun testApiEndpoint(urlString: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "测试API端点: $urlString")
                val url = URL(urlString)
                val connection = url.openConnection()
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.connect()
                connection.getInputStream().close()
                Log.d(TAG, "API端点可访问: $urlString")
                true
            } catch (e: IOException) {
                Log.e(TAG, "API端点不可访问: $urlString - ${e.message}")
                false
            }
        }
    }
}