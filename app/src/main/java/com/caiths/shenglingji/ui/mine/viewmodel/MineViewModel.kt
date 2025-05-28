package com.caiths.shenglingji.ui.mine.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caiths.shenglingji.ui.home.bean.GraphicCardBean

/**
 * 我的页面ViewModel
 */
class MineViewModel : ViewModel() {
    
    private val _graphicCardList = MutableLiveData<List<GraphicCardBean>>(emptyList())
    val graphicCardList: LiveData<List<GraphicCardBean>> = _graphicCardList
    
    /**
     * 加载数据
     */
    fun load() {
        // 模拟加载数据
        _graphicCardList.value = emptyList()
    }
}