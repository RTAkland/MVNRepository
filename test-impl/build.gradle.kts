plugins {
    kotlin("jvm")
}

group = "com.example"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("http://127.0.0.1:8088/releases")
}

dependencies {
    implementation("cn.rtast:test-publish-5:0.0.1")
}
