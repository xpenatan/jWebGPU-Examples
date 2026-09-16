plugins {
    alias(libs.plugins.androidLibrary)
}

android {
    namespace = "com.github.xpenatan.webgpu.backend.android"
    compileSdk = 36
    enableKotlin = false

    defaultConfig {
        minSdk = 29
    }

    flavorDimensions += "backend"
    productFlavors {
        create("wgpu") {
            dimension = "backend"
            buildConfigField("String", "JWEBGPU_BACKEND", "\"WGPU\"")
        }
        create("dawn") {
            dimension = "backend"
            buildConfigField("String", "JWEBGPU_BACKEND", "\"DAWN\"")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(project(":backend:core"))

    add("wgpuApi", libs.jWebGPUAndroidWgpu)
    add("dawnApi", libs.jWebGPUAndroidDawn)
}
