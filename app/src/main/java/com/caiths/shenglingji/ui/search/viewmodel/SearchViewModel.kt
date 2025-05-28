package com.caiths.shenglingji.ui.search.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caiths.shenglingji.ui.search.SearchDataRepository
import com.caiths.shenglingji.ui.search.bean.HotspotBean
import kotlinx.coroutines.launch

/**
 * Description:
 *
 * @Author: HuaJ1a
 * @Date: 2024/11/27
 */
class SearchViewModel: ViewModel() {
    
    val hotspotList = MutableLiveData<MutableList<HotspotBean>>()
    
    fun load() {
        viewModelScope.launch { 
            hotspotList.postValue(SearchDataRepository.getHotspotList())
        }
    }
    
}