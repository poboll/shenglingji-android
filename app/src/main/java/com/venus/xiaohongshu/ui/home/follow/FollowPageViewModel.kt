package com.venus.xiaohongshu.ui.home.follow

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.venus.xiaohongshu.ui.home.HomeDataRepository
import com.venus.xiaohongshu.ui.home.bean.UserBean
import kotlinx.coroutines.launch


/**
 * Description:
 *
 * @author: venus
 * @date: 2024/11/15
 */
class FollowPageViewModel(application: Application): AndroidViewModel(application) {

    private val repository = HomeDataRepository(application.applicationContext)
    
    var recommendUserList = MutableLiveData<MutableList<UserBean>>()
    
    fun load() {
        viewModelScope.launch { 
            recommendUserList.postValue(repository.getRecommendUserList())
        }
    }
}