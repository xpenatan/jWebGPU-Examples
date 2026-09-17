pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "jWebGPU-Examples"

include(":backend:core", ":backend:desktop", ":backend:web", ":backend:android")
include(":app:core", ":app:desktop-jni", ":app:desktop-ffm", ":app:web", ":app:android")

// Every module declares published dependencies. Only this explicit option replaces them.
if (providers.gradleProperty("useLocalJWebGPU").orNull?.toBoolean() == true) {
    val checkout = file(providers.gradleProperty("jWebGPUPath").getOrElse("../jWebGPU"))
    require(checkout.resolve("settings.gradle.kts").isFile) {
        "jWebGPU checkout not found at $checkout. Set -PjWebGPUPath=/path/to/jWebGPU."
    }
    includeBuild(checkout) {
        dependencySubstitution {
            val group = "com.github.xpenatan.jWebGPU"
            substitute(module("$group:webgpu-core")).using(project(":webgpu:core"))
            substitute(module("$group:webgpu-jni")).using(project(":webgpu:shared:jni"))
            substitute(module("$group:webgpu-desktop-jni")).using(project(":webgpu:desktop:jni"))
            substitute(module("$group:webgpu-desktop-ffm")).using(project(":webgpu:desktop:ffm"))
            substitute(module("$group:webgpu-web")).using(project(":webgpu:web:wasm"))
            substitute(module("$group:webgpu-android-wgpu")).using(project(":webgpu:android:jni"))
            substitute(module("$group:webgpu-android-dawn")).using(project(":webgpu:android:jni"))

            // Native jars are separate Maven modules but variants of the local projects.
            substitute(module("$group:webgpu-desktop-jni-wgpu_windows_x64")).using(project(":webgpu:desktop:jni"))
            substitute(module("$group:webgpu-desktop-jni-wgpu_linux_x64")).using(project(":webgpu:desktop:jni"))
            substitute(module("$group:webgpu-desktop-jni-wgpu_mac_x64")).using(project(":webgpu:desktop:jni"))
            substitute(module("$group:webgpu-desktop-jni-wgpu_mac_arm64")).using(project(":webgpu:desktop:jni"))
            substitute(module("$group:webgpu-desktop-jni-dawn_windows_x64")).using(project(":webgpu:desktop:jni"))
            substitute(module("$group:webgpu-desktop-jni-dawn_linux_x64")).using(project(":webgpu:desktop:jni"))
            substitute(module("$group:webgpu-desktop-jni-dawn_mac_x64")).using(project(":webgpu:desktop:jni"))
            substitute(module("$group:webgpu-desktop-jni-dawn_mac_arm64")).using(project(":webgpu:desktop:jni"))

            substitute(module("$group:webgpu-desktop-ffm-wgpu_windows_x64")).using(project(":webgpu:desktop:ffm"))
            substitute(module("$group:webgpu-desktop-ffm-wgpu_linux_x64")).using(project(":webgpu:desktop:ffm"))
            substitute(module("$group:webgpu-desktop-ffm-wgpu_mac_x64")).using(project(":webgpu:desktop:ffm"))
            substitute(module("$group:webgpu-desktop-ffm-wgpu_mac_arm64")).using(project(":webgpu:desktop:ffm"))
            substitute(module("$group:webgpu-desktop-ffm-dawn_windows_x64")).using(project(":webgpu:desktop:ffm"))
            substitute(module("$group:webgpu-desktop-ffm-dawn_linux_x64")).using(project(":webgpu:desktop:ffm"))
            substitute(module("$group:webgpu-desktop-ffm-dawn_mac_x64")).using(project(":webgpu:desktop:ffm"))
            substitute(module("$group:webgpu-desktop-ffm-dawn_mac_arm64")).using(project(":webgpu:desktop:ffm"))
        }
    }
}
