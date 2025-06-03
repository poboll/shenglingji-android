package com.venus.xiaohongshu.ui.home.follow

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * 测验视图模型
 * 管理测验数据和用户进度
 */
class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val sharedPreferences = context.getSharedPreferences("quiz_preferences", Context.MODE_PRIVATE)
    
    // 当前测验问题
    private val _currentQuiz = MutableLiveData<BirdQuiz?>(null)
    val currentQuiz: LiveData<BirdQuiz?> = _currentQuiz
    
    // 用户积分
    private val _userPoints = MutableLiveData<Int>()
    val userPoints: LiveData<Int> = _userPoints
    
    // 已识别鸟类数量
    private val _birdIdentifiedCount = MutableLiveData<Int>()
    val birdIdentifiedCount: LiveData<Int> = _birdIdentifiedCount
    
    // 图片加载状态
    private val _imageLoadingState = MutableLiveData<ImageLoadingState>(ImageLoadingState.IDLE)
    val imageLoadingState: LiveData<ImageLoadingState> = _imageLoadingState
    
    // 已完成的测验ID列表，避免重复出题
    private val completedQuizIds = mutableSetOf<String>()
    
    // 模拟鸟类题库
    private val birdQuizzes = mutableListOf<BirdQuiz>()
    
    init {
        // 从本地存储加载用户积分和进度
        loadUserProgress()
        // 初始化题库
        initializeQuizzes()
    }
    
    /**
     * 从本地存储加载用户积分和进度
     */
    private fun loadUserProgress() {
        val points = sharedPreferences.getInt("user_points", 0)
        val identifiedCount = sharedPreferences.getInt("bird_identified_count", 0)
        val completedIds = sharedPreferences.getStringSet("completed_quiz_ids", setOf()) ?: setOf()
        
        _userPoints.value = points
        _birdIdentifiedCount.value = identifiedCount
        completedQuizIds.addAll(completedIds)
        
        Log.d(TAG, "加载用户进度: 积分=$points, 已识别鸟类=$identifiedCount, 已完成测验=${completedIds.size}")
    }
    
    /**
     * 保存用户积分和进度到本地存储
     */
    private fun saveUserProgress() {
        sharedPreferences.edit().apply {
            putInt("user_points", _userPoints.value ?: 0)
            putInt("bird_identified_count", _birdIdentifiedCount.value ?: 0)
            putStringSet("completed_quiz_ids", completedQuizIds)
            apply()
        }
    }
    
    /**
     * 初始化测验题库
     */
    private fun initializeQuizzes() {
        // 模拟鸟类题库数据
        birdQuizzes.add(
            BirdQuiz(
                id = "bird_1",
                imageUrl = "https://dongniao.net/uimg/m/10325_DN_20231128100443_13216293_0.webp",
                options = listOf(
                    QuizOption("opt_1", "黄莺"),
                    QuizOption("opt_2", "金丝雀"),
                    QuizOption("opt_3", "黄腹地莺"),
                    QuizOption("opt_4", "黄鹂")
                ),
                correctOptionId = "opt_3",
                description = "黄腹地莺是一种小型鸟类，体长约12厘米，体重约9-11克。全身羽毛呈鲜黄色，胸部和腹部较亮。"
            )
        )
        
        birdQuizzes.add(
            BirdQuiz(
                id = "bird_2",
                imageUrl = "https://dongniao.net/uimg/m/783_DN_20240330105252_4572174576_0.webp",
                options = listOf(
                    QuizOption("opt_1", "火烈鸟"),
                    QuizOption("opt_2", "朱鹮"),
                    QuizOption("opt_3", "红鹮"),
                    QuizOption("opt_4", "红鹳")
                ),
                correctOptionId = "opt_3",
                description = "红鹮是一种中型涉禽，体长约56-61厘米，体重约650-700克。全身羽毛鲜红，嘴长而下弯。"
            )
        )
        
        birdQuizzes.add(
            BirdQuiz(
                id = "bird_3",
                imageUrl = "https://dongniao.net/images/p/p251.jpg",
                options = listOf(
                    QuizOption("opt_1", "蓝山雀"),
                    QuizOption("opt_2", "欧亚鳾"),
                    QuizOption("opt_3", "啄木鸟"),
                    QuizOption("opt_4", "蓝喜鹊")
                ),
                correctOptionId = "opt_2",
                description = "欧亚鳾是一种小型鸟类，体长约12-14.5厘米，体重约17-28克。上体蓝灰色，下体淡棕色，眼部有黑色眼罩。"
            )
        )
        
        birdQuizzes.add(
            BirdQuiz(
                id = "bird_4",
                imageUrl = "https://dongniao.net/images/p/p820.jpg",
                options = listOf(
                    QuizOption("opt_1", "长尾山雀"),
                    QuizOption("opt_2", "银喉长尾雀"),
                    QuizOption("opt_3", "白头翁"),
                    QuizOption("opt_4", "小太平鸟")
                ),
                correctOptionId = "opt_1",
                description = "长尾山雀是一种小型鸟类，体长约13-15厘米（其中尾长占一半），体重约7-10克。羽毛蓬松，尾巴极长，头部白色有黑色眉纹。"
            )
        )
        
        birdQuizzes.add(
            BirdQuiz(
                id = "bird_5",
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/5/5a/Erithacus_rubecula_with_cocked_head.jpg",
                options = listOf(
                    QuizOption("opt_1", "红胸鸲"),
                    QuizOption("opt_2", "知更鸟"),
                    QuizOption("opt_3", "红嘴相思鸟"),
                    QuizOption("opt_4", "赤胸鸟")
                ),
                correctOptionId = "opt_2",
                description = "知更鸟，又称欧亚鸲，是一种体型小巧的候鸟，体长约12.5-14厘米，翼展约20-22厘米，体重约16-22克。其胸部和面部为醒目的橙红色。"
            )
        )
        
        birdQuizzes.add(
            BirdQuiz(
                id = "bird_6",
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/Common_ostrich.jpg/800px-Common_ostrich.jpg",
                options = listOf(
                    QuizOption("opt_1", "鸸鹋"),
                    QuizOption("opt_2", "非洲鸵鸟"),
                    QuizOption("opt_3", "美洲鸵"),
                    QuizOption("opt_4", "鹤鸵")
                ),
                correctOptionId = "opt_2",
                description = "非洲鸵鸟是世界上体型最大的鸟类，特征为长颈、长腿、黑白色羽毛（雄鸟）和灰褐色羽毛（雌鸟和幼鸟）。"
            )
        )
        
        // 添加更多的鸟类测验...
        // 这里可以根据需要扩展更多的鸟类测验题
        
        Log.d(TAG, "初始化测验题库: ${birdQuizzes.size}道题目")
    }
    
    /**
     * 加载一道新测验题目
     */
    fun loadQuiz() {
        viewModelScope.launch {
            // 模拟加载延迟
            _currentQuiz.value = null
            _imageLoadingState.value = ImageLoadingState.LOADING
            delay(500)
            
            // 过滤掉已完成的测验
            val availableQuizzes = birdQuizzes.filter { it.id !in completedQuizIds }
            
            if (availableQuizzes.isEmpty()) {
                // 如果所有测验都已完成，重置并重新开始
                Log.d(TAG, "所有测验都已完成，重置测验进度")
                completedQuizIds.clear()
                _currentQuiz.value = birdQuizzes.random()
            } else {
                // 随机选择一道未完成的测验
                _currentQuiz.value = availableQuizzes.random()
            }
            
            // 设置图片为正在加载状态
            _imageLoadingState.value = ImageLoadingState.READY
            
            Log.d(TAG, "加载测验: ${_currentQuiz.value?.id}")
        }
    }
    
    /**
     * 提交答案
     */
    fun submitAnswer(optionId: String) {
        viewModelScope.launch {
            val quiz = _currentQuiz.value ?: return@launch
            
            if (optionId == quiz.correctOptionId) {
                // 答对了，加分并记录已识别鸟类
                val currentPoints = _userPoints.value ?: 0
                val currentIdentified = _birdIdentifiedCount.value ?: 0
                
                _userPoints.value = currentPoints + 5 // 每答对一题加5分
                
                // 如果是新识别的鸟类，计数器+1
                if (!completedQuizIds.contains(quiz.id)) {
                    _birdIdentifiedCount.value = currentIdentified + 1
                    completedQuizIds.add(quiz.id)
                }
                
                Log.d(TAG, "答对了测验: ${quiz.id}, 积分: ${_userPoints.value}, 已识别鸟类: ${_birdIdentifiedCount.value}")
            } else {
                // 答错了，不加分
                Log.d(TAG, "答错了测验: ${quiz.id}")
            }
            
            // 保存用户进度
            saveUserProgress()
            
            // 重置图片加载状态
            _imageLoadingState.value = ImageLoadingState.IDLE
            
            // 短暂延迟后加载下一题
            delay(1000)
            loadQuiz()
        }
    }
    
    /**
     * 跳过当前测验
     */
    fun skipQuiz() {
        viewModelScope.launch {
            val quiz = _currentQuiz.value ?: return@launch
            Log.d(TAG, "跳过测验: ${quiz.id}")
            
            // 重置图片加载状态
            _imageLoadingState.value = ImageLoadingState.IDLE
            
            // 短暂延迟后加载下一题
            delay(500)
            loadQuiz()
        }
    }
    
    /**
     * 通知图片加载成功
     */
    fun onImageLoadSuccess() {
        _imageLoadingState.value = ImageLoadingState.SUCCESS
        Log.d(TAG, "图片加载成功: ${_currentQuiz.value?.id}")
    }
    
    /**
     * 通知图片加载失败
     */
    fun onImageLoadFailure() {
        _imageLoadingState.value = ImageLoadingState.ERROR
        Log.d(TAG, "图片加载失败: ${_currentQuiz.value?.id}")
    }
    
    /**
     * 重置用户进度（用于测试）
     */
    fun resetProgress() {
        viewModelScope.launch {
            _userPoints.value = 0
            _birdIdentifiedCount.value = 0
            completedQuizIds.clear()
            saveUserProgress()
            Log.d(TAG, "重置用户进度")
        }
    }
    
    companion object {
        private const val TAG = "QuizViewModel"
    }
}

/**
 * 鸟类测验模型
 */
data class BirdQuiz(
    val id: String,
    val imageUrl: String,
    val options: List<QuizOption>,
    val correctOptionId: String,
    val description: String
)

/**
 * 测验选项模型
 */
data class QuizOption(
    val id: String,
    val text: String
)

/**
 * 图片加载状态
 */
enum class ImageLoadingState {
    IDLE,       // 初始状态
    LOADING,    // 加载中
    READY,      // 准备显示
    SUCCESS,    // 加载成功
    ERROR       // 加载失败
} 