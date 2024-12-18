plugins {
    id("application")
    id("io.ktor.plugin") version "3.0.2"
    id("com.github.node-gradle.node") version "3.2.1"
}

val exposedVersion: String by project
val h2Version: String by project
val logbackVersion: String by project

application {
    mainClass.set("cn.rtast.mvnrepo.ApplicationKt")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-auto-head-response-jvm")
    implementation("io.ktor:ktor-server-resources-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("com.h2database:h2:$h2Version")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.18.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
    implementation("org.jetbrains.kotlinx:kotlinx-cli-jvm:0.3.6")
}

sourceSets {
    main {
        resources {
            srcDir(project.layout.buildDirectory.dir("generated"))
        }
    }
}

tasks.named("processResources") {
    dependsOn("compileJava")
}

tasks.processResources {
    dependsOn(project(":mvnrepo-frontend").tasks.named("buildFrontend"))
    doLast {
        val generatedDir = file(project(":mvnrepo-frontend").layout.projectDirectory.dir("dist"))
        val staticDir = file(project(":mvnrepo-backend").layout.buildDirectory.dir("generated"))
        staticDir.deleteRecursively()
        generatedDir.copyRecursively(staticDir, overwrite = true)
    }
}