package com.venus.xiaohongshu.ui.user

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.venus.xiaohongshu.R
import com.venus.xiaohongshu.databinding.ActivityEditProfileBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: UserViewModel by viewModels()
    private val TAG = "EditProfileActivity"
    
    private var currentPhotoPath: String = ""
    private var selectedImageUri: Uri? = null
    private var newAvatarUrl: String? = null // 用于存储随机生成或已有的头像URL
    
    // 启动活动结果处理
    private val takePhotoResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = Uri.fromFile(File(currentPhotoPath))
            newAvatarUrl = null // 清除随机头像URL，因为选择了本地图片
            loadImageIntoView(selectedImageUri)
        }
    }
    
    private val pickImageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            newAvatarUrl = null // 清除随机头像URL，因为选择了本地图片
            loadImageIntoView(selectedImageUri)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        loadUserData()
        setupListeners()
        observeViewModel()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }
    
    private fun loadUserData() {
        val user = viewModel.getSavedUser()
        
        if (user != null) {
            // 设置当前资料信息
            binding.etNickname.setText(user.nickname ?: user.username)
            binding.etBio.setText(user.bio)
            binding.etPhone.setText(user.phone)
            
            // 设置性别
            when (user.gender) {
                "male" -> binding.rbMale.isChecked = true
                "female" -> binding.rbFemale.isChecked = true
                "other" -> binding.rbOtherGender.isChecked = true
            }
            
            // 加载头像
            if (!user.avatar.isNullOrEmpty()) {
                newAvatarUrl = user.avatar // 保存当前头像URL
                Glide.with(this)
                    .load(user.avatar)
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(binding.ivAvatar)
            }
        } else {
            // 如果没有用户数据，返回登录界面
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun setupListeners() {
        // 更换头像按钮点击事件
        binding.fabChangeAvatar.setOnClickListener {
            showAvatarOptionsDialog()
        }
        
        // 随机头像按钮点击事件
        binding.btnRandomAvatar.setOnClickListener {
            generateRandomAvatar()
        }
        
        // 随机昵称按钮点击事件
        binding.btnRandomNickname.setOnClickListener {
            generateRandomNickname()
        }
        
        // 保存按钮点击事件
        binding.btnSave.setOnClickListener {
            saveUserProfile()
        }
    }
    
    private fun observeViewModel() {
        viewModel.updateUserResult.observe(this) { result ->
            binding.progressBar.visibility = View.GONE
            
            result.onSuccess {
                Toast.makeText(this, R.string.update_success, Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            }.onFailure { e ->
                Log.e(TAG, "Update user failed", e)
                Toast.makeText(this, e.message ?: getString(R.string.update_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun showAvatarOptionsDialog() {
        val options = arrayOf(
            getString(R.string.take_photo),
            getString(R.string.choose_from_gallery),
            getString(R.string.random_avatar)
        )
        
        AlertDialog.Builder(this)
            .setTitle(R.string.change_avatar)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndOpenCamera()
                    1 -> openGallery()
                    2 -> generateRandomAvatar()
                }
            }
            .show()
    }
    
    private fun checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
        } else {
            openCamera()
        }
    }
    
    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // 确保有相机应用可以处理该意图
            takePictureIntent.resolveActivity(packageManager)?.also {
                // 创建照片文件
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // 错误处理
                    Toast.makeText(this, "无法创建图像文件", Toast.LENGTH_SHORT).show()
                    null
                }
                
                // 如果文件已创建，继续
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.venus.xiaohongshu.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    takePhotoResult.launch(takePictureIntent)
                }
            }
        }
    }
    
    private fun createImageFile(): File {
        // 创建图像文件名
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(null) ?: throw IOException("无法获取存储目录")
        
        return File.createTempFile(
            "JPEG_${timeStamp}_", // 前缀
            ".jpg", // 后缀
            storageDir // 目录
        ).apply {
            // 保存文件路径以供后续使用
            currentPhotoPath = absolutePath
        }
    }
    
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageResult.launch(intent)
    }
    
    private fun loadImageIntoView(uri: Uri?) {
        uri?.let {
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(binding.ivAvatar)
        }
    }
    
    private fun generateRandomAvatar() {
        selectedImageUri = null // 清除本地选择的图片
        newAvatarUrl = null     // 清除旧的随机URL，准备获取新的
        binding.progressBar.visibility = View.VISIBLE
        
        viewModel.fetchRandomAvatar { avatarUrlFetched ->
            binding.progressBar.visibility = View.GONE
            
            if (!avatarUrlFetched.isNullOrEmpty()) {
                Log.d(TAG, "成功获取随机头像 URL: $avatarUrlFetched")
                newAvatarUrl = avatarUrlFetched // 保存新的随机头像URL
                Glide.with(this)
                    .load(newAvatarUrl)
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(binding.ivAvatar)
                Log.d(TAG, "Random avatar loaded: $newAvatarUrl")
            } else {
                // 增强错误提示
                Log.e(TAG, "获取随机头像失败：返回的URL为空")
                Toast.makeText(
                    this, 
                    "生成随机头像失败，请检查网络连接或稍后再试", 
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun generateRandomNickname() {
        binding.progressBar.visibility = View.VISIBLE
        
        viewModel.fetchRandomNickname { nickname ->
            binding.progressBar.visibility = View.GONE
            if (!nickname.isNullOrEmpty()) {
                binding.etNickname.setText(nickname)
            } else {
                Toast.makeText(this, "生成随机昵称失败", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun saveUserProfile() {
        val nickname = binding.etNickname.text.toString().trim()
        val bio = binding.etBio.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        
        if (nickname.isEmpty()) {
            binding.tilNickname.error = getString(R.string.field_required)
            return
        } else {
            binding.tilNickname.error = null
        }
        
        // 获取选择的性别
        val selectedId = binding.rgGender.checkedRadioButtonId
        val gender = when {
            selectedId == -1 -> null // 未选择
            selectedId == binding.rbMale.id -> "male"
            selectedId == binding.rbFemale.id -> "female"
            else -> "other"
        }
        
        binding.progressBar.visibility = View.VISIBLE
        
        // 优先使用随机生成的头像URL
        if (!newAvatarUrl.isNullOrEmpty() && selectedImageUri == null) {
            Log.d(TAG, "Saving with new random avatar URL: $newAvatarUrl")
            updateUserProfile(nickname, bio, newAvatarUrl, gender, phone)
        } 
        // 其次，如果用户选择了新的本地图片，则上传它
        else if (selectedImageUri != null) {
            Log.d(TAG, "Uploading selected image URI: $selectedImageUri")
            viewModel.uploadAvatar(selectedImageUri!!) { uploadedUrl ->
                if (uploadedUrl != null) {
                    Log.d(TAG, "Avatar uploaded, URL: $uploadedUrl")
                    updateUserProfile(nickname, bio, uploadedUrl, gender, phone)
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "头像上传失败", Toast.LENGTH_SHORT).show()
                }
            }
        } 
        // 如果既没有新的随机URL，也没有选择本地图片，但之前加载过用户数据且有头像 (即newAvatarUrl在loadUserData时被赋值)
        // 这种情况意味着用户没有更改头像，所以应该使用之前加载的 newAvatarUrl (它可能是用户旧的头像URL)
        // 但为了避免不必要的更新（如果用户只是修改了昵称等），通常我们只在头像明确更改时才传递 avatar 参数
        // 这里我们假设如果 newAvatarUrl 和 selectedImageUri 都为null，我们就不传递 avatar (或传递当前的 viewModel.getSavedUser()?.avatar)
        // 为简单起见，如果没选择新图片，并且 newAvatarUrl 也是 null (例如，用户清除了随机头像后又没选别的)，则不更新头像。
        // 如果用户一开始就有头像，且没有做任何更改，则应该传递该旧头像URL。
        // viewModel.getSavedUser()?.avatar 可以作为未更改时的头像
        else {
            Log.d(TAG, "No new avatar selected. Updating profile without avatar change, or with existing avatar if any.")
            val existingAvatar = viewModel.getSavedUser()?.avatar // 获取当前用户已有的头像URL
            updateUserProfile(nickname, bio, existingAvatar, gender, phone)
        }
    }
    
    private fun updateUserProfile(
        nickname: String,
        bio: String?,
        avatar: String?, // 这个avatar参数现在可以是新的随机URL，或上传后的URL，或旧的URL
        gender: String?,
        phone: String?
    ) {
        Log.d(TAG, "Calling viewModel.updateUser with avatar: $avatar")
        viewModel.updateUser(nickname, bio, avatar, gender, null, phone)
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openCamera()
                } else {
                    Toast.makeText(this, "需要相机权限拍照", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
    
    companion object {
        private const val CAMERA_PERMISSION_REQUEST = 100
    }
}