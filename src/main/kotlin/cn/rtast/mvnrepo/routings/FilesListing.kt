/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo.routings

import cn.rtast.mvnrepo.REPOSITORIES
import cn.rtast.mvnrepo.STORAGE_PATH
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureFilesListing() {
    routing {
        REPOSITORIES.forEach {
            get(Regex("/$it/(.*)")) {
                val uri = call.request.uri
                val file = File(STORAGE_PATH, uri)
                call.respondFile(file)
            }
        }
    }
}