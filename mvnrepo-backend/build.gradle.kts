plugins {
    kotlin("multiplatform") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
}

val appVersion: String by project
val sqllinVersion: String by project
val ktorVersion: String by project

group = "cn.rtast"
version = appVersion

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

//dependencies {
//    add("kspCommonMainMetadata", "com.ctrip.kotlin:sqllin-processor:$sqllinVersion")
//}

kotlin {
    linuxX64()
    mingwX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
                implementation("com.ctrip.kotlin:sqllin-dsl:$sqllinVersion")
                implementation("com.ctrip.kotlin:sqllin-driver:$sqllinVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-cio:$ktorVersion")
                implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0-RC")
                implementation("com.ryanharter.kotlinx.serialization:kotlinx-serialization-xml:0.0.1-SNAPSHOT")
                implementation("io.ktor:ktor-server-auth:$ktorVersion")
//                implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.6.0")
                implementation("io.matthewnelson.kmp-file:file:0.1.1")
            }
        }
    }
}