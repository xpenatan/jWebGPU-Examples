plugins {
    id("java-library")
    alias(libs.plugins.gretty)
}

project.extra["webAppDir"] = layout.buildDirectory.dir("dist/webapp").get().asFile
gretty {
    contextPath = "/"
}

dependencies {
    implementation(project(":app:core"))
    implementation(project(":backend:web"))
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
}

val compileTeaVM = tasks.register<JavaExec>("compileTeaVM") {
    group = "build"
    description = "Compile the demos to JavaScript with TeaVM."
    mainClass.set("com.github.xpenatan.webgpu.demo.app.Build")
    classpath = sourceSets["main"].runtimeClasspath
}

val webBuild = tasks.register<Sync>("webgpu_demo_app_build") {
    group = "demos"
    description = "Build a complete browser distribution, including WebAssembly runtimes."
    dependsOn(compileTeaVM, configurations.runtimeClasspath)
    into(layout.buildDirectory.dir("dist/webapp"))
    from("src/main/resources/webapp")
    from(layout.buildDirectory.dir("teavm"))
    into("scripts") {
        from(provider {
            configurations.runtimeClasspath.get().files
                .filter { it.name.startsWith("webgpu-web_wasm-") || it.name.startsWith("runtime-web_wasm-") }
                .map { zipTree(it) }
        })
        include("*.js", "*.wasm")
        includeEmptyDirs = false
    }
    duplicatesStrategy = DuplicatesStrategy.FAIL
    doLast {
        for (resource in listOf("app.js", "scripts/jWebGPU.js", "scripts/jWebGPU.wasm",
                "scripts/runtime.js", "scripts/runtime.wasm")) {
            check(destinationDir.resolve(resource).isFile) { "Browser distribution is missing $resource" }
        }
    }
}

tasks.register("webgpu_demo_app_web_run") {
    group = "demos"
    description = "Build and serve the browser demos on localhost:8080."
    dependsOn(webBuild, "jettyRun")
}

afterEvaluate {
    tasks.named("prepareInplaceWebAppFolder") { dependsOn(webBuild) }
    tasks.named("jettyRun") { dependsOn(webBuild) }
}
