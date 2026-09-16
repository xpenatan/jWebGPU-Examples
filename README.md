# jWebGPU Examples

Standalone examples consuming [jWebGPU](https://github.com/xpenatan/jWebGPU) as a library: desktop JNI, desktop FFM, Android and TeaVM/WebGPU in the browser.

## Requirements

- JDK 25 to run Gradle and the FFM app. Shared Java sources target Java 8; web sources target Java 17.
- For Android: Android SDK platform 36 and a device/emulator running Android 10 or later. Set `ANDROID_HOME` or `sdk.dir` in an untracked `local.properties`.
- For web: a browser with WebGPU enabled. Use the localhost server below or HTTPS when hosting.

The Gradle wrapper is included. On Linux/macOS, use `./gradlew` instead of `.\gradlew.bat`.

## Published snapshots (default)

No jWebGPU source checkout or native compiler is required. Dependencies come from Maven Central and Sonatype's snapshot repository; `mavenLocal()` is deliberately not used. Versions are in [gradle/libs.versions.toml](gradle/libs.versions.toml).

```powershell
.\gradlew.bat projects
.\gradlew.bat :app:desktop-jni:webgpu_demo_app_desktop_jni_wgpu_run --refresh-dependencies
```

The examples require jWebGPU's backend-selection API, including `WGPUInstanceDescriptor.setBackendType`. If a published snapshot predates that API, use the local checkout below until an updated snapshot is published. Publishing is a library-maintainer operation, not a demo build step.

## Local jWebGPU development

Place the projects beside each other:

```text
java/
  jWebGPU/
  jWebGPU-Examples/
```

Enable the composite for a command:

```powershell
.\gradlew.bat -PuseLocalJWebGPU=true :app:desktop-jni:webgpu_demo_app_desktop_jni_wgpu_run
```

Or set `useLocalJWebGPU=true` in this project's `gradle.properties`. `-PjWebGPUPath=E:/path/to/jWebGPU` overrides the sibling location. This option substitutes the Java modules **and matching native payloads**; jParser and other dependencies still come from repositories. No publishing to Maven Local is required.

Generate the local library bindings and build the selected platform's native bridge in the **jWebGPU** checkout first. For example, after its documented Windows dependency downloads:

```powershell
.\gradlew.bat :webgpu:builder:jParser_generate :webgpu:builder:jParser_build_windows64_jni_wgpu
```

Use the library's corresponding FFM, Android or WebAssembly build task when targeting those runtimes. Composite builds package existing native outputs; they do not download SDKs or invoke native compilers automatically. Rebuild native outputs when changing the bindings. The local checkout must include the outgoing native variants introduced with this demo extraction.

## Desktop

Run one bridge/backend combination:

```powershell
.\gradlew.bat :app:desktop-jni:webgpu_demo_app_desktop_jni_wgpu_run
.\gradlew.bat :app:desktop-jni:webgpu_demo_app_desktop_jni_dawn_run
.\gradlew.bat :app:desktop-ffm:webgpu_demo_app_desktop_ffm_wgpu_run
.\gradlew.bat :app:desktop-ffm:webgpu_demo_app_desktop_ffm_dawn_run
```

Append `--args=triangle` to choose a demo. Other names: `vertex`, `buffers`, `cube`, `cone`, `sphere`, `torus`, `studio` (default). Close the window to exit. Native artifacts are selected for Windows x64, Linux x64, macOS x64 or macOS arm64.

## Browser

```powershell
.\gradlew.bat :app:web:webgpu_demo_app_web_run
```

Open `http://localhost:8080/`. Select a demo using a query, for example `http://localhost:8080/?demo=buffers`. Stop the server with Ctrl+C.

To build without starting a server:

```powershell
.\gradlew.bat :app:web:webgpu_demo_app_build
```

Deploy the complete `app/web/build/dist/webapp/` directory, including `scripts/`. The web entry page is tracked in [app/web/src/main/resources/webapp/index.html](app/web/src/main/resources/webapp/index.html).

## Android

```powershell
adb devices -l
.\gradlew.bat :app:android:installWgpuDebug
adb shell am start -n com.github.xpenatan.webgpu.demo/.app.MainAndroid
```

For Dawn, use `:app:android:installDawnDebug`. Both flavors have the same application ID; install one at a time. Choose a demo from the app's selector.

For compilation/package checks without a device:

```powershell
.\gradlew.bat :app:android:assembleWgpuDebug :app:android:assembleDawnDebug
```

The manually triggered Google Play workflow lives in [.github/workflows/publish_android.yml](.github/workflows/publish_android.yml). It consumes published library artifacts. Release signing and Play publishing require that repository's signing/service-account secrets; debug builds require neither.

## How dependencies are separated

`backend/core` uses `compileOnlyApi(webgpu-core)` so shared code can compile against the portable API. JNI/FFM apps obtain the real Java implementation from their chosen runtime; adding the core stubs to their runtime classpath can shadow the implementation and produce null results. Desktop launch tasks check for duplicate `WGPU` implementations before starting. `verifyRuntimeClasspath` can run this check independently in either desktop app module.

The browser is different: `webgpu-web` brings the core API plus `gen.web` replacements that TeaVM applies while compiling. It also requires the separate `webgpu-web_wasm` runtime payload. Android selects exactly one backend AAR per flavor because both package `libjWebGPU.so`.

`app/core` contains the eight demos. `backend/*` contains their small platform hosts; `app/*` contains launchers and platform packaging.

## Startup fallback

The WGPU examples try Vulkan then D3D12 on Windows, Vulkan then OpenGL on Linux, Metal on macOS, and Vulkan then OpenGLES on Android. Each reported startup failure releases the attempt's resources and tries a fresh instance. Retries end after the first successfully rendered frame. Dawn and the browser use their default backend selection.

An in-process fallback cannot recover from a native process crash. These examples do not persist a backend choice across launches.

## License

[Apache License 2.0](LICENSE), inherited from jWebGPU.
