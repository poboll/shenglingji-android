package com.venus.xiaohongshu.mock

import android.content.Context
import com.venus.xiaohongshu.R
import com.venus.xiaohongshu.ui.home.bean.UserBean
import com.venus.xiaohongshu.ui.home.bean.UserInfoBean
import kotlin.random.Random
import java.util.UUID

/**
 * Description:
 *
 * @author: venus
 * @date: 2024/11/15
 */
object UserMock {
    private val nameList = listOf(
        "Alice", "李华", "Bob", "王明", "Charlie", "张伟",
        "David", "赵丽", "Emma", "刘洋", "Frank", "陈静",
        "Grace", "李娜", "Henry", "王刚", "Isabella", "杨柳",
        "Jack", "周杰", "Karen", "吴敏", "Leo", "郑涛",
        "Megan", "陈强", "Nicholas", "林琳", "Olivia", "黄勇",
        "Peter", "孙莉", "Quinn", "朱军", "Rachel", "李梅",
        "Samuel", "王红", "Tiffany", "周丽", "Uma", "赵刚",
        "Victor", "陈丽", "Wendy", "李杰", "Xavier", "王芳",
        "Yara", "张强", "Zachary", "刘梅", "Amelia", "陈涛",
        "Benjamin", "李芳", "Chloe", "王杰", "Daniel", "赵敏",
        "Evelyn", "刘刚", "Finn", "陈丽", "Georgia", "李勇",
        "Harrison", "王梅", "Ivy", "张丽", "Jacob", "刘涛"
    )
    
    private val imageList = listOf(
        R.drawable.p1,
        R.drawable.p2,
        R.drawable.p3,
        R.drawable.p4,
        R.drawable.p5,
        R.drawable.p6,
        R.drawable.p7,
        R.drawable.p8,
        R.drawable.p9,
        R.drawable.p10,
        R.drawable.p11
    )
    
    private val userInfoList = listOf(
        UserInfoBean(age = 18, sex = 1, address = "北京市天安门"),
        UserInfoBean(age = 22, sex = 1, address = "上海市陆家嘴"),
        UserInfoBean(age = 23, sex = 0, address = "重庆市"),
        UserInfoBean(age = 20, sex = 1, address = "广州市珠江新城"),
        UserInfoBean(age = 36, sex = 0, address = "深圳市深圳湾科技园"),
        UserInfoBean(age = 30, sex = 1, address = "大理市venus民宿"),
    )

    fun getRandomName(): String {
        return nameList[Random.nextInt(nameList.size)]
    }
    
    fun getRandomImage(): Int {
        return imageList[Random.nextInt(imageList.size)]
    }
    
    fun gerRandomUserInfo(): UserInfoBean {
        return userInfoList[Random.nextInt(userInfoList.size)]
    }
    
    // 提供随机用户对象，用于与新的数据模型集成
    fun provideRandomUser(context: Context): UserBean {
        val userId = UUID.randomUUID().toString()
        val name = getRandomName()
        val image = getRandomImage()
        val userInfo = gerRandomUserInfo()
        
        return UserBean(
            id = userId,
            name = name,
            image = image,
            userInfo = userInfo,
            userName = name,
            userAvatar = "drawable://$image" // 将资源ID转换为类似URL的格式
        )
    }
}