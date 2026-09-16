plugins {
    id("java")
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
}

val lwjglWindowsNatives = "natives-windows"
val lwjglLinuxNatives = "natives-linux"
val lwjglMacNatives = "natives-macos"
val lwjglMacArmNatives = "natives-macos-arm64"

dependencies {
    api(project(":backend:core"))

    compileOnly(libs.jWebGPUCore)

    implementation(platform(libs.lwjglBom))
    implementation(libs.lwjglCore)
    implementation(libs.lwjglGlfw)
    implementation(libs.lwjglOpengl)
    runtimeOnly(variantOf(libs.lwjglCore) { classifier(lwjglWindowsNatives) })
    runtimeOnly(variantOf(libs.lwjglGlfw) { classifier(lwjglWindowsNatives) })
    runtimeOnly(variantOf(libs.lwjglOpengl) { classifier(lwjglWindowsNatives) })
    runtimeOnly(variantOf(libs.lwjglCore) { classifier(lwjglLinuxNatives) })
    runtimeOnly(variantOf(libs.lwjglGlfw) { classifier(lwjglLinuxNatives) })
    runtimeOnly(variantOf(libs.lwjglOpengl) { classifier(lwjglLinuxNatives) })
    runtimeOnly(variantOf(libs.lwjglCore) { classifier(lwjglMacNatives) })
    runtimeOnly(variantOf(libs.lwjglGlfw) { classifier(lwjglMacNatives) })
    runtimeOnly(variantOf(libs.lwjglOpengl) { classifier(lwjglMacNatives) })
    runtimeOnly(variantOf(libs.lwjglCore) { classifier(lwjglMacArmNatives) })
    runtimeOnly(variantOf(libs.lwjglGlfw) { classifier(lwjglMacArmNatives) })
    runtimeOnly(variantOf(libs.lwjglOpengl) { classifier(lwjglMacArmNatives) })
}
