# 应用签名指南

## 签名信息

- **域名**: caiths.com
- **密钥库文件**: `app/keystore/shenglingji-release.keystore`
- **密钥别名**: shenglingji
- **密钥库密码**: android
- **密钥密码**: android
- **有效期**: 10000天

## 手动构建签名APK

1. 打开Android Studio
2. 在菜单中选择 Build > Generate Signed Bundle / APK
3. 选择 APK
4. 在"Key store path"中选择 `app/keystore/shenglingji-release.keystore`
5. 填写Key store password: `android`
6. 填写Key alias: `shenglingji`
7. 填写Key password: `android`
8. 点击Next
9. 选择release构建类型
10. 勾选V1和V2签名
11. 点击Finish完成构建

## 在build.gradle中使用此签名配置（如需要）

如果将来需要在build.gradle中添加签名配置，可以添加以下代码：

```kotlin
signingConfigs {
    release {
        storeFile file("keystore/shenglingji-release.keystore")
        storePassword "android"
        keyAlias "shenglingji"
        keyPassword "android"
    }
}

buildTypes {
    release {
        signingConfig signingConfigs.release
        // 其他配置...
    }
}
```

## 将签名APK安装到手机

1. 使用USB数据线连接手机与电脑
2. 确保手机上已开启USB调试模式
3. 使用以下命令安装应用：

```
adb install -r app/build/outputs/apk/release/app-release.apk
```

## 通过网络分发APK

如果要通过网络分发APK，请确保：

1. 将生成的APK上传到您的服务器
2. 创建一个下载页面，链接指向APK
3. 在手机浏览器中访问 `https://shenglingji.caiths.com/download` 等下载地址
4. 下载并安装APK（需要允许安装未知来源的应用） 