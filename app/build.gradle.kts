plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.venus.xiaohongshu"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.venus.xiaohongshu"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.fragment.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.compose.runtime.livedata)

    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("io.coil-kt.coil3:coil-compose:3.0.3")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.3")
    // 基础依赖包，必须要依赖
    implementation("com.geyifeng.immersionbar:immersionbar:3.2.2")
    // kotlin扩展（可选）
    implementation("com.geyifeng.immersionbar:immersionbar-ktx:3.2.2")
    // fragment快速实现（可选）已废弃
    implementation("com.geyifeng.immersionbar:immersionbar-components:3.2.2")
    
    implementation("androidx.media3:media3-exoplayer:1.5.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.5.0")
    implementation("androidx.media3:media3-ui:1.5.0")

    val cameraxVersion = "1.5.0-alpha03"
    implementation ("androidx.camera:camera-core:${cameraxVersion}")
    implementation ("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation ("androidx.camera:camera-view:${cameraxVersion}")
    implementation ("androidx.camera:camera-lifecycle:$cameraxVersion")
    
    // 添加Retrofit和相关依赖
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0") 
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    
    // 添加Gson和Retrofit-Gson转换器
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // 图像加载库
    implementation("com.github.bumptech.glide:glide:4.15.1")
    
    // 圆形图像视图
    implementation("de.hdodenhof:circleimageview:3.1.0")
    
    // Material Components
    implementation("com.google.android.material:material:1.10.0")

    // 网络请求依赖
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    
    // 图片加载
    implementation("io.coil-kt.coil3:coil:3.0.0-alpha01")
    implementation("io.coil-kt.coil3:coil-compose:3.0.0-alpha01")
    
    // 协程
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    
    // 测试依赖
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}