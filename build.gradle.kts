plugins {
    kotlin("jvm") version "1.9.23"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(11)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.9.0")
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

application {
    mainClass.set("app.MainKt")
}

tasks {
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources"))
        archiveClassifier.set("fatjar") // Naming the jar
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) }
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) } + sourcesMain.output
        from(contents)
    }
    build {
        dependsOn(fatJar)
    }
}