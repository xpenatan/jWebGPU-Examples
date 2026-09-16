# jWebGPU Examples

Desktop, Android and browser examples for [jWebGPU](https://github.com/xpenatan/jWebGPU).

## Setup

Use **JDK 25**. Android also needs SDK 36 and an Android 10+ device; the browser examples need WebGPU support.

```sh
git clone https://github.com/xpenatan/jWebGPU-Examples.git
cd jWebGPU-Examples
```

Set `useLocalJWebGPU=false` in [gradle.properties](gradle.properties) to download dependencies. Choose a compatible snapshot or release with `jWebGPUVersion` in [gradle/libs.versions.toml](gradle/libs.versions.toml). No jWebGPU checkout or native compiler is needed.

Run the commands below from this folder. On Linux/macOS, replace `.\gradlew.bat` with `./gradlew`. Add `--refresh-dependencies` when updating a snapshot.

## Desktop

```powershell
.\gradlew.bat :app:desktop-jni:webgpu_demo_app_desktop_jni_wgpu_run
```

For FFM:

```powershell
.\gradlew.bat :app:desktop-ffm:webgpu_demo_app_desktop_ffm_wgpu_run
```

For Dawn, change the task suffix from `_wgpu_run` to `_dawn_run`.

Studio opens by default. Append `--args=triangle` to choose a demo; also available: `vertex`, `buffers`, `cube`, `cone`, `sphere`, `torus` and `studio`.

## Browser

```powershell
.\gradlew.bat :app:web:webgpu_demo_app_web_run
```

Open http://localhost:8080/ or select a demo with `?demo=triangle`. Stop with Ctrl+C. To build for hosting, run `:app:web:webgpu_demo_app_build` and deploy the complete `app/web/build/dist/webapp/` folder over HTTPS.

## Android

Set `ANDROID_HOME` or `sdk.dir` in `local.properties`, then connect a device or start an emulator:

```powershell
adb devices -l
.\gradlew.bat :app:android:installWgpuDebug
adb shell am start -n com.github.xpenatan.webgpu.demo/.app.MainAndroid
```

For Dawn, use `:app:android:installDawnDebug`. Install one flavor at a time; both use the same application ID.

## Local jWebGPU development

To use a local checkout, set `useLocalJWebGPU=true` and `jWebGPUPath=../jWebGPU` in [gradle.properties](gradle.properties). Adjust the path if needed.

First [generate the bindings and build the target's native bridge](https://github.com/xpenatan/jWebGPU#building-from-source) in jWebGPU. Then return here and run the same commands. `includeBuild` supplies the local Java modules and native payloads.

## Startup fallback

WGPU tries Vulkan then D3D12 on Windows, Vulkan then OpenGL on Linux, Metal on macOS, and Vulkan then OpenGLES on Android. A reported startup failure triggers cleanup and the next attempt; native process crashes cannot be retried in-process. Dawn and the browser use their default backend selection.

[Apache License 2.0](LICENSE)
