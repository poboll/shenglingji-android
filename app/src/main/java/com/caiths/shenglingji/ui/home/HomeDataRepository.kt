package com.caiths.shenglingji.ui.home

import com.caiths.shenglingji.R
import com.caiths.shenglingji.mock.ImageMock
import com.caiths.shenglingji.mock.TitleMock
import com.caiths.shenglingji.mock.UserMock
import com.caiths.shenglingji.mock.VideoMock
import com.caiths.shenglingji.ui.home.bean.GraphicCardBean
import com.caiths.shenglingji.ui.home.bean.GraphicCardType
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
object HomeDataRepository {
    
    // 发现图文卡
    private val graphicCardList = mutableListOf<GraphicCardBean>()
    // 同城图文卡
    private val cityGraphicCardList = mutableListOf<GraphicCardBean>()
    
    suspend fun getGraphicCardList(reload: Boolean = false): MutableList<GraphicCardBean> {
        if (graphicCardList.isEmpty() || reload) {
            val tempList = mutableListOf<GraphicCardBean>()
            repeat(200) {
                val type = if(Random.nextDouble() < 0.2) GraphicCardType.VIDEO else GraphicCardType.NORMAL
                val graphicCardBean = GraphicCardBean(
                    id = UUID.randomUUID().toString(),
                    title = TitleMock.getRandomTitle(),
                    imageUrl = ImageMock.getRandomImage(),
                    type = type
                )
                tempList.add(graphicCardBean)
                delay(3)
            }
            graphicCardList.clear()
            graphicCardList.addAll(tempList)
        }
        return graphicCardList
    }

    suspend fun getCityGraphicCardList(reload: Boolean = false): MutableList<GraphicCardBean> {
        if (cityGraphicCardList.isEmpty() || reload) {
            val tempList = mutableListOf<GraphicCardBean>()
            repeat(200) {
                val graphicCardBean = GraphicCardBean(
                    id = UUID.randomUUID().toString(),
                    title = TitleMock.getRandomTitle(),
                    imageUrl = ImageMock.getRandomImage()
                )
                tempList.add(graphicCardBean)
                delay(5)
            }
            cityGraphicCardList.clear()
            cityGraphicCardList.addAll(tempList)
        }
        return cityGraphicCardList
    }

    suspend fun getRecommendUserList(): MutableList<UserBean> {
        val recommendUserList = mutableListOf<UserBean>()
        repeat(10) {
            val user = UserBean(
                id = UUID.randomUUID().toString(),
                name = UserMock.getRandomName(),
                image = UserMock.getRandomImage()
            )
            recommendUserList.add(user)
            delay(50)
        }
        return recommendUserList
    }
    
}