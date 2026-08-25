import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val localProperties = Properties()
localProperties.load(
    FileInputStream(rootProject.file("local.properties"))
)

val kakaoNativeAppKey =
    localProperties.getProperty("KAKAO_NATIVE_APP_KEY")

val kakaoRestApiKey =
    localProperties.getProperty("KAKAO_REST_API_KEY")

android {
    namespace = "com.example.clouddx_team4_project"

    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.clouddx_team4_project"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"

        // 카카오맵 Native App Key
        buildConfigField(
            "String",
            "KAKAO_NATIVE_APP_KEY",
            "\"$kakaoNativeAppKey\""
        )

        // 카카오 Local REST API Key
        buildConfigField(
            "String",
            "KAKAO_REST_API_KEY",
            "\"$kakaoRestApiKey\""
        )
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(
        "androidx.compose.material:material-icons-extended:1.7.8"
    )

    // Navigation
    implementation(
        "androidx.navigation:navigation-compose:2.9.5"
    )

    // ========================================
    // Kakao Maps SDK
    // ========================================

    implementation(
        "com.kakao.maps.open:android:2.14.1"
    )

    // Key Hash 확인용 Kakao Common SDK
    implementation(
        "com.kakao.sdk:v2-common:2.24.0"
    )

    // 현재 위치 확인
    implementation(
        "com.google.android.gms:play-services-location:21.3.0"
    )

    // ========================================
    // Kakao Local API - Retrofit
    // ========================================

    implementation(
        "com.squareup.retrofit2:retrofit:2.11.0"
    )

    implementation(
        "com.squareup.retrofit2:converter-gson:2.11.0"
    )

    // ========================================
    // Test
    // ========================================

    testImplementation(libs.junit)

    androidTestImplementation(
        platform(libs.androidx.compose.bom)
    )

    androidTestImplementation(
        libs.androidx.compose.ui.test.junit4
    )

    androidTestImplementation(
        libs.androidx.espresso.core
    )

    androidTestImplementation(
        libs.androidx.junit
    )

    debugImplementation(
        libs.androidx.compose.ui.test.manifest
    )

    debugImplementation(
        libs.androidx.compose.ui.tooling
    )
}