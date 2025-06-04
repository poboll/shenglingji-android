// 定义包名，标识工具类的命名空间
package com.venus.xiaohongshu.utils

// 导入Android工具类Patterns，提供常用的正则表达式模式
import android.util.Patterns

/**
 * 验证邮箱地址格式是否有效
 * 使用Android内置的邮箱地址正则表达式进行验证
 * 
 * @param email 待验证的邮箱地址字符串
 * @return true表示邮箱格式有效，false表示格式无效
 */
fun isValidEmail(email: String): Boolean {
    // 使用Android内置的EMAIL_ADDRESS模式匹配器验证邮箱格式
    // Patterns.EMAIL_ADDRESS包含了标准的邮箱地址正则表达式
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}