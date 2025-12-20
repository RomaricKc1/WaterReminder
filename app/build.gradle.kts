import org.gradle.api.tasks.testing.logging.TestExceptionFormat
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
        versionCode = 2
        versionName = "1.1"
        version = "1.1"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "com.romarickc.reminder.HiltTestRunner"
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
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

tasks.withType<Test> {
    this.testLogging {
        this.showStandardStreams = true
        this.exceptionFormat = TestExceptionFormat.FULL
        this.events("passed", "failed", "skipped", "started")
    }
}

dependencies {
    ksp(libs.room.compiler)
    kapt(libs.hilt.android.compiler)
    kapt(libs.hilt.compiler)

    implementation(libs.rules)
    implementation(libs.wear.tooling.preview)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.found)
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

    implementation(libs.runner)
    implementation(libs.test.core)

    debugImplementation(libs.compose.ui.tooling)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.compose.ui.test.manifest)
    androidTestImplementation(libs.kotlin.test)
    androidTestImplementation(libs.hilt.android.testing)
}
