plugins {
    kotlin("jvm") version "2.1.0"
}

val appVersion: String by project

allprojects {
    apply {
        apply(plugin = "org.jetbrains.kotlin.jvm")
    }

    group = "cn.rtast"
    version = appVersion

    repositories {
        mavenCentral()
    }
}