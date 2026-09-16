import java.util.zip.ZipFile

plugins {
    base
}

subprojects {
    if (name == "desktop-jni" || name == "desktop-ffm") {
        plugins.withId("java") {
            val runtime = configurations.named("runtimeClasspath")
            val expectedRuntime = if (name == "desktop-jni") "webgpu-jni-" else "webgpu-desktop-ffm-"
            val verifyRuntime = tasks.register("verifyRuntimeClasspath") {
                group = "verification"
                description = "Reject missing or duplicate WGPU implementations, including core API stubs."
                dependsOn(runtime)
                doLast {
                    val entry = "com/github/xpenatan/webgpu/WGPU.class"
                    val implementations = runtime.get().files.filter { file ->
                        if (file.isDirectory) file.resolve(entry).isFile
                        else if (file.extension == "jar") ZipFile(file).use { it.getEntry(entry) != null }
                        else false
                    }
                    check(implementations.size == 1 && implementations.single().name.startsWith(expectedRuntime)) {
                        "Expected one platform WGPU implementation, without core stubs. Found: $implementations"
                    }
                    logger.lifecycle("WGPU runtime implementation: ${implementations.single()}")
                }
            }
            tasks.named("check") { dependsOn(verifyRuntime) }
            tasks.withType<JavaExec>().configureEach { dependsOn(verifyRuntime) }
        }
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://central.sonatype.com/repository/maven-snapshots/")
        maven("https://teavm.org/maven/repository/")
    }
    configurations.configureEach {
        resolutionStrategy.cacheChangingModulesFor(0, "seconds")
    }
}
