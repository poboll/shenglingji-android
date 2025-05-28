package com.caiths.shenglingji.activity.graphic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caiths.shenglingji.mock.TitleMock
import com.caiths.shenglingji.mock.UserMock
import com.caiths.shenglingji.ui.home.HomeDataRepository
import com.caiths.shenglingji.ui.home.bean.CommentBean
import com.caiths.shenglingji.ui.home.bean.GraphicCardBean
import com.caiths.shenglingji.ui.home.bean.UserBean
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

/**
 * Description:
 *
 * @Author: HuaJ1a
 * @Date: 2024/11/28
 */
class GraphicViewModel: ViewModel() {
    
    var id: String = ""
    var graphicCardBean: GraphicCardBean? = null
    var commentList = mutableListOf<CommentBean>()
    
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
            
            // 生成模拟评论数据
            commentList.clear()
            repeat(10) {
                val user = UserBean(
                    id = UUID.randomUUID().toString(),
                    name = UserMock.getRandomName(),
                    image = UserMock.getRandomImage()
                )
                val comment = CommentBean(
                    id = UUID.randomUUID().toString(),
                    title = TitleMock.getRandomTitle(),
                    user = user,
                    likes = Random.nextInt(0, 1000)
                )
                commentList.add(comment)
            }
        }
    }
    
}