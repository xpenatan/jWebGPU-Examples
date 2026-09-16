plugins {
    id("java")
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
}

dependencies {
    api(project(":backend:core"))

    // TeaVM replaces the core API with gen.web implementations while compiling.
    api(libs.jWebGPUWeb)
    runtimeOnly(libs.jWebGPUWebWasm) {
        capabilities { requireCapability("com.github.xpenatan.jWebGPU:webgpu-web_wasm") }
    }
    implementation(libs.jMultiplatform)
}
