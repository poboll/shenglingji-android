package com.caiths.shenglingji.ui.publish.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caiths.shenglingji.ui.publish.controller.AlbumController
import kotlinx.coroutines.launch

/**
 * Description:
 *
 * @Author: HuaJ1a
 * @Date: 2024/11/27
 */
class PublishViewModel: ViewModel() {
    
    var albumController: AlbumController? = null
    val albumSelectList = MutableLiveData<MutableList<String>>() // 照片选择
    
    fun init(context: Context) {
        viewModelScope.launch {
            albumController = AlbumController(context)
        }
    }
    
    fun addSelect(uri: String) {
        val list = albumSelectList.value?.toMutableList() ?: mutableListOf()
        list.add(uri)
        albumSelectList.postValue(list)
    }

    fun removeSelect(uri: String) {
        val list = albumSelectList.value?.toMutableList() ?: return
        if (list.contains(uri)) {
            list.remove(uri)
        }
        albumSelectList.postValue(list)
    }
    
}