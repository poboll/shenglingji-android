package com.caiths.shenglingji.ui.mine

import com.caiths.shenglingji.mock.ImageMock
import com.caiths.shenglingji.mock.TitleMock
import com.caiths.shenglingji.mock.UserMock
import com.caiths.shenglingji.ui.home.bean.GraphicCardBean
import com.caiths.shenglingji.ui.home.bean.UserBean
import kotlinx.coroutines.delay
import java.util.UUID
import kotlin.random.Random

/**
 * Description: home 数据仓库，代替网络请求
 *
 * @author: venus
 * @date: 2024/11/15
 */
object MineDataRepository {
    
    // 发现图文卡
    private val graphicCardList = mutableListOf<GraphicCardBean>()
    
    suspend fun getGraphicCardList(): MutableList<GraphicCardBean> {
        if (graphicCardList.isEmpty()) {
            repeat(50) {
                val graphicCardBean = GraphicCardBean(
                    id = UUID.randomUUID().toString(),
                    title = TitleMock.getRandomTitle(),
                    imageUrl = ImageMock.getRandomImage(),
                )
                graphicCardList.add(graphicCardBean)
                delay(1)
            }
        }
        return graphicCardList
    }
}