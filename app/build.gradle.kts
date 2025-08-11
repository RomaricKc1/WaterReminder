import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kapt)
}

android {
    namespace = "com.romarickc.reminder"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.romarickc.reminder"
        minSdk = 30
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/database_schemas")
    }

    kapt {
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    ksp(libs.room.compiler)
    kapt(libs.hilt.android.compiler)
    kapt(libs.hilt.compiler)

    implementation(libs.wear.tooling.preview)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.fund)
    implementation(libs.compose.material)
    implementation(libs.compose.foundation)
    implementation(libs.compose.navigation)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.hilt.android)
    implementation(libs.horologist.compose.tools)
    implementation(libs.horologist.tiles)
    implementation(libs.material.icons.core)
    implementation(libs.material.icons.extended)
    implementation(libs.material)
    implementation(libs.core.ktx)
    implementation(libs.play.service)
    implementation(libs.perc.layout)
    implementation(libs.legacy.v4)
    implementation(libs.recyclerview)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.datastore.preferences)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.work)
    implementation(libs.wear)
    implementation(libs.coil.compose)
    implementation(libs.runtime.livedata)
    implementation(libs.guava)
    implementation(libs.work.runtime)
    implementation(libs.accompanist.permissions)

    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.compose.ui.test.manifest)

    debugImplementation(libs.compose.ui.tooling)
}
