import org.apache.tools.ant.taskdefs.condition.Os

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
    implementation("io.github.cdimascio:dotenv-kotlin:6.3.1")
    implementation("dev.onvoid.webrtc:webrtc-java:0.8.0")
    implementation(getWebrtcDependency())
    implementation("com.corundumstudio.socketio:netty-socketio:2.0.9")
}

application {
    mainClass.set("app.MainKt")
}

tasks {
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources"))
        archiveFileName.set("${project.name}-fatjar.jar")
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

fun getWebrtcDependency(): String {
    val osArch = System.getProperty("os.arch")
    if(Os.isFamily(Os.FAMILY_MAC)){
        if(osArch == "x86_64" || osArch == "amd64"){
            return "dev.onvoid.webrtc:webrtc-java:0.8.0:macos-x86_64"
        } else if(osArch == "aarch64"){
            return "dev.onvoid.webrtc:webrtc-java:0.8.0:macos-aarch64"
        } else {
            throw Error("Error, unsupported MacOS arch: $osArch")
        }
    } else if(Os.isFamily(Os.FAMILY_WINDOWS)){
        if(osArch == "x86_64" || osArch == "amd64"){
            return "dev.onvoid.webrtc:webrtc-java:0.8.0:windows-x86_64"
        } else {
            throw Error("Error, unsupported Windows arch: $osArch")
        }
    } else if(Os.isFamily(Os.FAMILY_UNIX)){
        if(osArch == "x86_64" || osArch == "amd64"){
            return "dev.onvoid.webrtc:webrtc-java:0.8.0:linux-x86_64"
        } else if(osArch == "aarch64"){
            return "dev.onvoid.webrtc:webrtc-java:0.8.0:linux-aarch64"
        } else if(osArch == "aarch32"){
            return "dev.onvoid.webrtc:webrtc-java:0.8.0:linux-aarch32"
        }else {
            throw Error("Error, unsupported Unix arch: $osArch")
        }
    } else {
        throw Error("Error, unsupported OS")
    }
}
