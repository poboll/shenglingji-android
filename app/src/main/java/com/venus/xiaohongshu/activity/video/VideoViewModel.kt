package com.venus.xiaohongshu.activity.video

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.venus.xiaohongshu.ui.home.HomeDataRepository
import com.venus.xiaohongshu.ui.home.bean.GraphicCardBean
import com.venus.xiaohongshu.ui.home.bean.GraphicCardType
import kotlinx.coroutines.launch

/**
 * Description:
 *
 * @Author: HuaJ1a
 * @Date: 2024/11/28
 */
class VideoViewModel(application: Application): AndroidViewModel(application) {

    private val repository = HomeDataRepository(application.applicationContext)
    
    var id: String = ""
    var graphicCardBean: GraphicCardBean? = null
    var videoList = mutableListOf<GraphicCardBean>()

    fun init() {
        if (id.isEmpty()) {
            return
        }
        viewModelScope.launch {
            val graphicCardList = repository.getGraphicCardList()
            graphicCardList.forEach { card ->
                if (card.id == id) {
                    graphicCardBean = card
                }
            }
            videoList = graphicCardList.filter { card ->
                card.type == GraphicCardType.Video
            }.toMutableList()
            
            graphicCardBean?.let {
                videoList.add(0, it)
            }
        }
    }
}