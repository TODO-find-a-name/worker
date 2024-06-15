plugins {
    kotlin("jvm") version "1.9.23"
}

group = "com.todo.todo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":libs:common"))
    implementation("io.socket:socket.io-client:2.1.0")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("dev.onvoid.webrtc:webrtc-java:0.8.0")
    //implementation("dev.onvoid.webrtc:webrtc-java:0.8.0:windows-x86_64")
    //implementation("dev.onvoid.webrtc:webrtc-java:0.8.0:macos-x86_64")
    implementation("dev.onvoid.webrtc:webrtc-java:0.8.0:macos-aarch64")
    //implementation("dev.onvoid.webrtc:webrtc-java:0.8.0:linux-x86_64")
    //implementation("dev.onvoid.webrtc:webrtc-java:0.8.0:linux-aarch64")
    //implementation("dev.onvoid.webrtc:webrtc-java:0.8.0:linux-aarch32")
}
