plugins {
    kotlin("jvm") version "1.9.23"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":modules:js-module"))
    implementation(project(":libs:common"))
    implementation(project(":libs:core"))
}

kotlin {
    jvmToolchain(21)
}