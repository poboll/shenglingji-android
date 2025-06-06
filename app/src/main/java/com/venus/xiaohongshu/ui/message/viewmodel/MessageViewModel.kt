package com.venus.xiaohongshu.ui.message.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.venus.xiaohongshu.ui.message.MessageDataRepository
import com.venus.xiaohongshu.ui.message.bean.MessageBean
import com.venus.xiaohongshu.ui.message.bean.RecommendFriendBean

/**
 * Description:
 *
 * @author: poboll
 * @date: 2024/06/01
 */
class MessageViewModel: ViewModel() {
    
    val messageList = MutableLiveData<MutableList<MessageBean>>()
    
    val recommendList = MutableLiveData<MutableList<RecommendFriendBean>>()
    
    fun load() {
        messageList.postValue(MessageDataRepository.getMessageList())
        recommendList.postValue(MessageDataRepository.getRecommendList())
    }
}