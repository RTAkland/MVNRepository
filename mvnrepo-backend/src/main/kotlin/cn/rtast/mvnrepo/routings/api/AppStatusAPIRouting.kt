/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/20
 */


package cn.rtast.mvnrepo.routings.api

import cn.rtast.mvnrepo.EXCLUDE_FILES
import cn.rtast.mvnrepo.STARTUP_TIME
import cn.rtast.mvnrepo.STORAGE_PATH_
import cn.rtast.mvnrepo.entity.api.AppStatus
import cn.rtast.mvnrepo.entity.api.StorageOverview
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.nio.file.Files
import java.time.Instant
import kotlin.io.path.Path
import kotlin.io.path.fileSize
import kotlin.io.path.name

fun Application.configureAppStatusAPIRouting() {
    routing {
        authenticate("auth-jwt") {
            get("/-/api/status") {
                call.respond(getSystemInfo())
            }
            get("/-/api/storage") {
                call.respond(calculateUsedSize())
            }
        }
    }
}

private fun getSystemInfo(): AppStatus {
    val runtime = Runtime.getRuntime()
    val totalMemory = runtime.totalMemory()
    val freeMemory = runtime.freeMemory()
    val maxMemory = runtime.maxMemory()
    val osName = System.getProperty("os.name")
    val osVersion = System.getProperty("os.version")
    val osArch = System.getProperty("os.arch")
    val kotlinVersion = KotlinVersion.CURRENT.toString()
    val jvmVersion = Runtime.version().toString()
    val currentSecond = Instant.now().epochSecond
    val uptime = currentSecond - STARTUP_TIME
    return AppStatus(
        jvmVersion, totalMemory, freeMemory,
        maxMemory, kotlinVersion, osName,
        osArch, osVersion, uptime
    )
}

private fun calculateUsedSize(): StorageOverview {
    var totalSize = 0L
    Files.walk(Path(STORAGE_PATH_)).use { stream ->
        stream.filter { Files.isRegularFile(it) }
            .forEach { file ->
                if (file.fileName.name !in EXCLUDE_FILES) totalSize += file.fileSize()
            }
    }
    return StorageOverview(totalSize)
}