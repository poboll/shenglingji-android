package com.venus.xiaohongshu.ui.mine.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.venus.xiaohongshu.data.model.User
import com.venus.xiaohongshu.mock.UserMock
import com.venus.xiaohongshu.ui.home.HomeDataRepository
import com.venus.xiaohongshu.ui.home.bean.GraphicCardBean
import com.venus.xiaohongshu.ui.home.bean.UserBean
import com.venus.xiaohongshu.utils.SessionManager
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Description:
 *
 * @author: venus
 * @date: 2024/11/21
 */
class MineViewModel(application: Application) : AndroidViewModel(application) {
    
    private val sessionManager = SessionManager(application)
    private val repository = HomeDataRepository(application.applicationContext)
    
    // 获取保存的用户信息
    fun getSavedUser(): User? {
        return sessionManager.getUser()
    }
    
    // 如果没有登录用户，则使用模拟数据
    val user = UserBean(
        id = getSavedUser()?.id?.toString() ?: UUID.randomUUID().toString(),
        name = getSavedUser()?.nickname ?: getSavedUser()?.username ?: UserMock.getRandomName(),
        image = UserMock.getRandomImage()
    )

    var graphicCardList = MutableLiveData<MutableList<GraphicCardBean>>()

    fun load() {
        viewModelScope.launch {
            graphicCardList.postValue(repository.getGraphicCardList())
        }
    }
    
    // 检查用户是否已登录
    fun isLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }
    
    // 工厂类，用于在Compose中创建ViewModel
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as Application
                MineViewModel(application)
            }
        }
    }
}