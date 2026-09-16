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
            mapOf(
                "webgpu-core" to ":webgpu:core",
                "webgpu-jni" to ":webgpu:shared:jni",
                "webgpu-desktop-jni" to ":webgpu:desktop:jni",
                "webgpu-desktop-ffm" to ":webgpu:desktop:ffm",
                "webgpu-web" to ":webgpu:web:wasm",
                "webgpu-android-wgpu" to ":webgpu:android:jni",
                "webgpu-android-dawn" to ":webgpu:android:jni"
            ).forEach { (artifact, path) ->
                substitute(module("$group:$artifact")).using(project(path))
            }
            // Native jars are separate Maven modules but variants of the local projects.
            for (bridge in listOf("jni", "ffm")) {
                for (backend in listOf("wgpu", "dawn")) {
                    for (platform in listOf("windows_x64", "linux_x64", "mac_x64", "mac_arm64")) {
                        val coordinate = "$group:webgpu-desktop-$bridge-${backend}_$platform"
                        substitute(module(coordinate)).using(project(":webgpu:desktop:$bridge"))
                    }
                }
            }
            substitute(module("$group:webgpu-web_wasm")).using(project(":webgpu:web:wasm"))
        }
    }
}
