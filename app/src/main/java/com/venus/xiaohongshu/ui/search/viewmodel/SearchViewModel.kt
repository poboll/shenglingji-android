package com.venus.xiaohongshu.ui.search.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.venus.xiaohongshu.data.Post
import com.venus.xiaohongshu.network.RetrofitClient
import com.venus.xiaohongshu.ui.home.bean.GraphicCardBean
import com.venus.xiaohongshu.ui.search.SearchDataRepository
import com.venus.xiaohongshu.ui.search.bean.HotspotBean
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 搜索视图模型
 */
class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "SearchViewModel"
    
    // 热门搜索列表
    val hotspotList = MutableLiveData<MutableList<HotspotBean>>()
    
    // 搜索结果
    private val _searchResults = MutableLiveData<List<Post>>()
    val searchResults: LiveData<List<Post>> = _searchResults
    
    // 搜索结果卡片（兼容首页格式）
    private val _searchResultCards = MutableLiveData<List<GraphicCardBean>>()
    val searchResultCards: LiveData<List<GraphicCardBean>> = _searchResultCards
    
    // 搜索历史
    private val _searchHistory = MutableLiveData<List<String>>()
    val searchHistory: LiveData<List<String>> = _searchHistory
    
    // 当前搜索关键词
    private val _currentQuery = MutableLiveData<String>()
    val currentQuery: LiveData<String> = _currentQuery
    
    // 加载状态
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    // 错误信息
    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage
    
    // 当前搜索筛选器
    private var currentType: Int? = null
    private var currentSort: String = "relevance"
    private var currentMediaType: String? = null
    
    // 分页
    private var currentPage = 1
    private val pageSize = 20
    private val _hasMoreData = MutableLiveData(true)
    val hasMoreData: LiveData<Boolean> = _hasMoreData
    
    init {
        loadSearchHistory()
        load()
    }
    
    /**
     * 加载热门搜索列表
     */
    fun load() {
        viewModelScope.launch { 
            hotspotList.postValue(SearchDataRepository.getHotspotList())
        }
    }
    
    /**
     * 搜索帖子
     * @param query 搜索关键词
     * @param type 帖子类型：1=植物，2=动物，null=全部
     * @param sort 排序方式：relevance=相关性, newest=最新, oldest=最早, popular=热门
     * @param mediaType 媒体类型：image=图片, video=视频, null=全部
     * @param forceRefresh 是否强制刷新（忽略当前结果重新搜索）
     */
    fun search(
        query: String,
        type: Int? = currentType,
        sort: String = currentSort,
        mediaType: String? = currentMediaType,
        forceRefresh: Boolean = false
    ): Job {
        // 如果搜索参数没变且不是强制刷新，就不重新搜索
        if (!forceRefresh && query == _currentQuery.value && 
            type == currentType && sort == currentSort && mediaType == currentMediaType) {
            return viewModelScope.launch { /* 不执行任何操作 */ }
        }
        
        // 保存搜索参数
        _currentQuery.value = query
        currentType = type
        currentSort = sort
        currentMediaType = mediaType
        
        // 重置分页
        currentPage = 1
        _hasMoreData.value = true
        
        // 保存搜索历史
        if (query.isNotEmpty()) {
            addToSearchHistory(query)
        }
        
        return performSearch()
    }
    
    /**
     * 加载更多结果（下一页）
     */
    fun loadMore() {
        if (_isLoading.value == true || _hasMoreData.value == false) {
            return
        }
        
        currentPage++
        performSearch(isLoadMore = true)
    }
    
    /**
     * 执行搜索
     */
    private fun performSearch(isLoadMore: Boolean = false): Job {
        val query = _currentQuery.value ?: return viewModelScope.launch { /* 空搜索 */ }
        
        _isLoading.value = true
        _errorMessage.value = null
        
        return viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.searchPosts(
                    query = query,
                    type = currentType,
                    page = currentPage,
                    limit = pageSize,
                    sort = currentSort,
                    mediaType = currentMediaType
                )
                
                if (response.success) {
                    // 检查是否还有更多数据
                    _hasMoreData.value = currentPage < response.data.totalPages
                    
                    // 更新搜索结果
                    val newPosts = response.data.posts
                    
                    if (isLoadMore) {
                        // 加载更多模式：合并现有结果
                        val currentPosts = _searchResults.value ?: emptyList()
                        _searchResults.value = currentPosts + newPosts
                    } else {
                        // 新搜索模式：替换现有结果
                        _searchResults.value = newPosts
                    }
                    
                    // 将帖子转换为GraphicCardBean以兼容首页UI格式
                    val cards = newPosts.map { post -> 
                        GraphicCardBean.fromPost(post) 
                    }
                    
                    if (isLoadMore) {
                        val currentCards = _searchResultCards.value ?: emptyList()
                        _searchResultCards.value = currentCards + cards
                    } else {
                        _searchResultCards.value = cards
                    }
                    
                } else {
                    _errorMessage.value = "搜索失败: 未知错误"
                    Log.e(TAG, "搜索失败，API返回错误状态")
                }
            } catch (e: Exception) {
                _errorMessage.value = "搜索出错: ${e.message}"
                Log.e(TAG, "搜索请求失败", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 重置筛选器
     */
    fun resetFilters() {
        currentType = null
        currentSort = "relevance" 
        currentMediaType = null
        
        // 如果有当前搜索词，则重新搜索
        _currentQuery.value?.let {
            if (it.isNotEmpty()) {
                search(it, forceRefresh = true)
            }
        }
    }
    
    /**
     * 更新筛选条件并重新搜索
     */
    fun updateFilters(type: Int? = currentType, sort: String = currentSort, mediaType: String? = currentMediaType) {
        _currentQuery.value?.let {
            if (it.isNotEmpty()) {
                search(it, type, sort, mediaType, forceRefresh = true)
            }
        }
    }
    
    // 搜索历史相关方法
    private val SEARCH_HISTORY_PREFS = "search_history_prefs"
    private val SEARCH_HISTORY_KEY = "search_history"
    private val MAX_HISTORY_ITEMS = 10
    
    /**
     * 加载搜索历史
     */
    private fun loadSearchHistory() {
        val sharedPrefs = getApplication<Application>()
            .getSharedPreferences(SEARCH_HISTORY_PREFS, Context.MODE_PRIVATE)
        
        val historyString = sharedPrefs.getString(SEARCH_HISTORY_KEY, "")
        val history = if (historyString.isNullOrEmpty()) {
            listOf()
        } else {
            historyString.split(",").filter { it.isNotEmpty() }
        }
        
        _searchHistory.value = history
    }
    
    /**
     * 添加搜索记录到历史
     */
    private fun addToSearchHistory(query: String) {
        val currentHistory = _searchHistory.value?.toMutableList() ?: mutableListOf()
        
        // 如果已经存在，先移除旧记录
        currentHistory.remove(query)
        
        // 添加到最前面
        currentHistory.add(0, query)
        
        // 保持历史记录数量不超过限制
        val trimmedHistory = currentHistory.take(MAX_HISTORY_ITEMS)
        _searchHistory.value = trimmedHistory
        
        // 保存到 SharedPreferences
        val historyString = trimmedHistory.joinToString(",")
        getApplication<Application>()
            .getSharedPreferences(SEARCH_HISTORY_PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(SEARCH_HISTORY_KEY, historyString)
            .apply()
    }
    
    /**
     * 清空搜索历史
     */
    fun clearSearchHistory() {
        _searchHistory.value = emptyList()
        
        getApplication<Application>()
            .getSharedPreferences(SEARCH_HISTORY_PREFS, Context.MODE_PRIVATE)
            .edit()
            .remove(SEARCH_HISTORY_KEY)
            .apply()
    }
    
    /**
     * 删除单条搜索历史
     */
    fun removeHistoryItem(query: String) {
        val currentHistory = _searchHistory.value?.toMutableList() ?: return
        currentHistory.remove(query)
        _searchHistory.value = currentHistory
        
        // 保存到 SharedPreferences
        val historyString = currentHistory.joinToString(",")
        getApplication<Application>()
            .getSharedPreferences(SEARCH_HISTORY_PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(SEARCH_HISTORY_KEY, historyString)
            .apply()
    }
}