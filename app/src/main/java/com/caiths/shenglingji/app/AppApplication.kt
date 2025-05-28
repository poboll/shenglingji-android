package com.caiths.shenglingji.app

import android.app.Application
import android.content.Context

/**
 * Description: 应用程序类
 *
 * @author: venus
 * @date: 2024/11/15
 */
const val TAG = "shenglingji-app"

class AppApplication: Application() {
    
    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}