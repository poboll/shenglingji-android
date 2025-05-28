package com.caiths.shenglingji.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caiths.shenglingji.ui.home.bean.UserInfoBean

class HomeViewModel : ViewModel() {
    private val _users = MutableLiveData<List<UserInfoBean>>()
    val users: LiveData<List<UserInfoBean>> = _users
    
    fun loadData() {
        // TODO: Load data from repository
    }
} 