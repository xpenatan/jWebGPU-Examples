import org.gradle.api.GradleException
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    id("java")
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
}

dependencies {
    implementation(project(":app:core"))
    implementation(project(":backend:desktop"))

    api(libs.jWebGPUJni)
    api(libs.jWebGPUDesktopJni)
}

val mainClassName = "com.github.xpenatan.webgpu.demo.app.Main"

enum class DemoBackend(val id: String, val systemValue: String) {
    WGPU("wgpu", "WGPU"),
    DAWN("dawn", "DAWN"),
}

fun currentDesktopPlatformName(): String {
    val os = DefaultNativePlatform.getCurrentOperatingSystem()
    val archName = DefaultNativePlatform.getCurrentArchitecture().name.lowercase()
    return when {
        os.isWindows -> "windows_x64"
        os.isLinux -> "linux_x64"
        os.isMacOsX && (archName.contains("aarch64") || archName.contains("arm64")) -> "mac_arm64"
        os.isMacOsX -> "mac_x64"
        else -> throw GradleException("Unsupported desktop platform: ${os.name} $archName")
    }
}

val currentDesktopPlatform = currentDesktopPlatformName()

fun registerDesktopRunTask(taskName: String, backend: DemoBackend, descriptionSuffix: String) {
    val backendRuntime = configurations.create("${backend.id}Runtime") {
        isCanBeConsumed = false
        extendsFrom(configurations.runtimeClasspath.get())
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        }
    }
    val artifactId = "webgpu-desktop-jni-${backend.id}_$currentDesktopPlatform"
    val coordinate = "${libs.versions.jWebGPUGroup.get()}:$artifactId"
    dependencies.add(backendRuntime.name,
        "$coordinate:${libs.versions.jWebGPUSnapshot.get()}") {
        capabilities { requireCapability(coordinate) }
    }
    tasks.register<JavaExec>(taskName) {
        group = "demos"
        description = "Run desktop demo using JNI bridge with $descriptionSuffix backend"
        mainClass.set(mainClassName)
        classpath = sourceSets["main"].output + backendRuntime
        systemProperty("jwebgpu.bridge", "JNI")
        jvmArgs("--enable-native-access=ALL-UNNAMED")
        systemProperty("jwebgpu.backend", backend.systemValue)
        systemProperty("studio.capture", "false")
        systemProperty("studio.capture.shadow", "false")
        systemProperty("studio.capture.shadow.diagnostics", "false")
        systemProperty("studio.capture.max", "1")
        systemProperty("studio.capture.path", layout.buildDirectory.dir("captures").get().asFile.absolutePath)


        if(DefaultNativePlatform.getCurrentOperatingSystem().isMacOsX) {
            jvmArgs("-XstartOnFirstThread")
        }
    }
}

registerDesktopRunTask("webgpu_demo_app_desktop_jni_wgpu_run", DemoBackend.WGPU, "WGPU")
registerDesktopRunTask("webgpu_demo_app_desktop_jni_dawn_run", DemoBackend.DAWN, "Dawn")
