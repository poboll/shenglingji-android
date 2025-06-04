<p align="center">
  <img src="https://github.com/poboll/shenglingji-android/raw/main/app/src/main/res/drawable/icon_logo.png" width="120" height="120" alt="生灵集 Android">
</p>

<h1 align="center">生灵集 Android</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-brightgreen" alt="Platform">
  <img src="https://img.shields.io/badge/Kotlin-1.9.0-blue" alt="Kotlin">
  <img src="https://img.shields.io/badge/Jetpack%20Compose-1.5.1-blueviolet" alt="Jetpack Compose">
  <img src="https://img.shields.io/badge/Min%20SDK-24-orange" alt="Min SDK">
  <img src="https://img.shields.io/badge/Target%20SDK-35-yellow" alt="Target SDK">
</p>

<p align="center">基于 Jetpack Compose 构建的现代化动植物科普社交应用</p>

## 📱 应用介绍

「生灵集」Android 客户端是一款专注于动植物科普和自然探索的社交应用，采用全新的 Jetpack Compose UI 框架开发，提供流畅、美观的用户体验。应用融合了社交分享、知识学习和互动问答等功能，让用户在探索自然奥秘的同时，也能与志同道合的自然爱好者交流互动。

## 🛠️ 技术架构

### 架构模式

应用采用 MVVM (Model-View-ViewModel) 架构模式，结合 Jetpack 组件库，实现了清晰的关注点分离：

```
app/
├── ui/                # 视图层 (Compose UI)
│   ├── common/        # 通用 UI 组件
│   ├── home/          # 首页相关界面
│   ├── search/        # 搜索相关界面
│   ├── publish/       # 发布内容界面
│   └── mine/          # 个人中心界面
├── data/              # 数据层
│   ├── model/         # 数据模型
│   ├── repository/    # 数据仓库
│   └── api/           # API 接口定义
├── network/           # 网络层
│   └── RetrofitClient.kt  # Retrofit 网络客户端
└── utils/             # 工具类
```

### 核心技术栈

- **UI 框架**：Jetpack Compose
- **状态管理**：ViewModel + LiveData + Compose State
- **网络请求**：Retrofit2 + OkHttp3
- **图片加载**：Coil 3.0 + Glide 4.15
- **视频播放**：Media3 ExoPlayer 1.5.0
- **异步处理**：Kotlin Coroutines
- **相机功能**：CameraX 1.5.0-alpha03
- **状态栏沉浸**：ImmersionBar 3.2.2

## ✨ 特色功能

### 现代化 UI 实现

- **瀑布流布局**：使用 `LazyVerticalStaggeredGrid` 实现高效的瀑布流内容展示
- **自定义视图**：包含多种自定义 Compose 组件，如动画加载指示器、多状态按钮等
- **动画效果**：丰富的过渡动画和交互反馈，提升用户体验
- **主题定制**：支持浅色/深色主题，以及动态颜色系统

### 高级交互功能

- **下拉刷新**：集成 Material 3 的 `PullToRefreshContainer` 组件
- **滑动冲突处理**：优化嵌套滚动体验
- **吸顶效果**：实现滚动时的内容吸顶效果
- **手势识别**：支持复杂的触摸手势操作

### 知识学习模块

- **动植物科普**：丰富的动植物知识内容展示
- **互动问答**：趣味性的自然知识问答和测验
- **物种图鉴**：分类展示各类动植物信息

### 社交功能

- **内容发布**：支持图文、视频等多媒体内容发布
- **互动系统**：评论、点赞、收藏等社交互动功能
- **关注机制**：用户关注与粉丝系统
- **个人主页**：展示用户发布的内容和收藏

## 📸 界面展示

<table>
  <tr>
    <td><img src="https://raw.githubusercontent.com/poboll/shenglingji/main/public/images/screenshots/home_plants.png" width="200" alt="植物首页"></td>
    <td><img src="https://raw.githubusercontent.com/poboll/shenglingji/main/public/images/screenshots/home_animals.png" width="200" alt="动物首页"></td>
    <td><img src="https://raw.githubusercontent.com/poboll/shenglingji/main/public/images/screenshots/shopping.png" width="200" alt="购物页面"></td>
  </tr>
  <tr>
    <td><img src="https://raw.githubusercontent.com/poboll/shenglingji/main/public/images/screenshots/search.png" width="200" alt="搜索"></td>
    <td><img src="https://raw.githubusercontent.com/poboll/shenglingji/main/public/images/screenshots/profile.png" width="200" alt="个人中心"></td>
    <td><img src="https://raw.githubusercontent.com/poboll/shenglingji/main/public/images/screenshots/settings.png" width="200" alt="设置"></td>
  </tr>
</table>

## 🚀 快速开始

### 环境要求

- Android Studio Hedgehog | 2023.1.1 或更高版本
- JDK 11 或更高版本
- Android SDK 35
- Gradle 8.4 或更高版本

### 构建步骤

1. 克隆仓库
```bash
git clone https://github.com/poboll/shenglingji-android.git
```

2. 在 Android Studio 中打开项目

3. 同步 Gradle 文件

4. 构建并运行应用
```bash
./gradlew build
```

## 🔌 后端连接

应用默认连接到本地开发服务器：

```kotlin
// RetrofitClient.kt
private const val BASE_URL_EMULATOR = "http://10.0.2.2:3000/api/"
private const val BASE_URL_DEVICE = "http://192.168.1.100:3000/api/"
```

可以根据实际环境修改 `BASE_URL_DEVICE` 地址。

## 📚 技术亮点

### Compose UI 实践

- **组合优先**：充分利用 Compose 的组合模式，构建可重用的 UI 组件
- **单向数据流**：采用现代化的状态管理方式，确保 UI 状态的一致性
- **声明式 UI**：使用声明式方法构建复杂界面，减少样板代码

### 性能优化

- **懒加载**：使用 `LazyColumn` 和 `LazyVerticalStaggeredGrid` 实现高效列表
- **图片优化**：结合 Coil 的内存缓存和磁盘缓存策略
- **视图复用**：优化 Compose 重组范围，减少不必要的重绘

### 代码质量

- **模块化设计**：功能模块清晰分离，便于维护和扩展
- **一致的编码风格**：遵循 Kotlin 官方编码规范
- **注释完善**：关键代码和复杂逻辑配有详细注释

## 🤝 参与贡献

欢迎参与项目开发，提交 Pull Request 或提出 Issue！

1. Fork 本仓库
2. 创建你的特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交你的更改 (`git commit -m 'feat: add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 开启一个 Pull Request

## 📄 开源许可

本项目采用 MIT 许可证 - 详情请参阅 [LICENSE](../LICENSE) 文件

## 🔗 相关链接

- [主项目仓库](https://github.com/poboll/shenglingji)
- [后端服务仓库](https://github.com/poboll/shenglingji-backend)

---

<p align="center">使用 Jetpack Compose 构建的现代化 Android 应用</p>





