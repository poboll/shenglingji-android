package com.venus.xiaohongshu

import com.venus.xiaohongshu.utils.NetworkUtils
import org.junit.Test
import org.junit.Assert.*

/**
 * 测试NetworkUtils的URL转换功能
 */
class NetworkUtilsTest {
    
    @Test
    fun testConvertLocalhostUrl() {
        // 测试localhost URL转换
        val localhostUrl = "http://localhost:3000/uploads/images/plant-1.png"
        val expectedUrl = "http://10.0.2.2:3000/uploads/images/plant-1.png"
        val actualUrl = NetworkUtils.convertLocalhostUrl(localhostUrl)
        assertEquals(expectedUrl, actualUrl)
        
        // 测试非localhost URL不变
        val normalUrl = "http://example.com/image.png"
        val unchangedUrl = NetworkUtils.convertLocalhostUrl(normalUrl)
        assertEquals(normalUrl, unchangedUrl)
        
        // 测试null URL
        val nullUrl = NetworkUtils.convertLocalhostUrl(null)
        assertNull(nullUrl)
        
        // 测试空字符串
        val emptyUrl = NetworkUtils.convertLocalhostUrl("")
        assertEquals("", emptyUrl)
    }
}