/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/20
 */


package cn.rtast.mvnrepo.routings.api

import cn.rtast.mvnrepo.entity.api.AppStatus
import cn.rtast.mvnrepo.util.str.toJson
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureAppStatusAPIRouting() {
    routing {
        authenticate("authenticate") {
            get("/-/api/status") {
                call.respondText(getSystemInfo())
            }
        }
    }
}

private fun getSystemInfo(): String {
    val runtime = Runtime.getRuntime()
    val totalMemory = runtime.totalMemory() / 1024 / 1024
    val freeMemory = runtime.freeMemory() / 1024 / 1024
    val maxMemory = runtime.maxMemory() / 1024 / 1024
    val osName = System.getProperty("os.name")
    val osVersion = System.getProperty("os.version")
    val osArch = System.getProperty("os.arch")
    val kotlinVersion = KotlinVersion.CURRENT.toString()
    val jvmVersion = Runtime.version().toString()
    return AppStatus(jvmVersion, totalMemory, freeMemory, maxMemory, kotlinVersion, osName, osArch, osVersion).toJson()
}
