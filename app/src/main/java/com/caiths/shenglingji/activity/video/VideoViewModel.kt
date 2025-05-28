package com.caiths.shenglingji.activity.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caiths.shenglingji.ui.home.HomeDataRepository
import com.caiths.shenglingji.ui.home.bean.GraphicCardBean
import com.caiths.shenglingji.ui.home.bean.GraphicCardType
import kotlinx.coroutines.launch

/**
 * Description:
 *
 * @Author: HuaJ1a
 * @Date: 2024/11/28
 */
class VideoViewModel: ViewModel() {

    var id: String = ""
    var graphicCardBean: GraphicCardBean? = null
    var videoList = mutableListOf<GraphicCardBean>()

    fun init() {
        if (id.isEmpty()) {
            return
        }
        viewModelScope.launch {
            HomeDataRepository.getGraphicCardList().forEach {
                if (it.id == id) {
                    graphicCardBean = it
                }
            }
            videoList = HomeDataRepository.getGraphicCardList().filter {
                it.type == GraphicCardType.VIDEO
            }.toMutableList()
            videoList.add(0, graphicCardBean!!)
        }
    }
}