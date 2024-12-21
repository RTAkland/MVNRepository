plugins {
    id("application")
    alias(libs.plugins.ktor)
}

application {
    mainClass.set("cn.rtast.mvnrepo.ApplicationKt")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.logback.classic)
    implementation(libs.gson)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.dataformat.xml)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.kotlinx.cli)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auto.head.response)
    implementation(libs.ktor.server.resources)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.netty)
    implementation(libs.sqlite.jdbc)
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
        val generatedDir = file(project(":mvnrepo-frontend").layout.projectDirectory.dir("docs/.vitepress/dist"))
        val staticDir = file(project(":mvnrepo-backend").layout.buildDirectory.dir("generated"))
        staticDir.deleteRecursively()
        generatedDir.copyRecursively(staticDir, overwrite = true)
    }
}