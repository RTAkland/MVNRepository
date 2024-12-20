plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.node)
}

val appVersion: String by project

allprojects {
    apply {
        apply(plugin = "org.jetbrains.kotlin.jvm")
        apply(plugin = "com.github.node-gradle.node")
    }

    group = "cn.rtast"
    version = appVersion

    repositories {
        mavenCentral()
    }
}