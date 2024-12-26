/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/26
 */


package cn.rtast.mvnrepo.routings.api

import cn.rtast.mvnrepo.configManager
import cn.rtast.mvnrepo.entity.config.Config
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureConfigAPIRouting() {
    routing {
        get("/-/api/settings") {
            call.respond(configManager.readConfig())
        }

        authenticate("auth-jwt") {
            put("/-/api/settings") {
                val update = call.receive<Map<String, Any?>>()
                val currentConfig = configManager.readConfig()
                val updatedConfig = updateConfig(currentConfig, update).apply {
                    configManager.writeConfig(this)
                }
                call.respond(updatedConfig)
            }
        }
    }
}


fun updateConfig(currentConfig: Config, updates: Map<String, Any?>): Config {
    val updatedSettings = currentConfig.settings.apply {
        updates.forEach { (key, value) ->
            this[key] = value
        }
    }
    return Config(updatedSettings)
}