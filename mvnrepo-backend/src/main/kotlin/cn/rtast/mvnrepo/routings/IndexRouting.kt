/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/19
 */


package cn.rtast.mvnrepo.routings

import cn.rtast.mvnrepo.util.respondHTMLResources
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureIndexRouting() {
    routing {
        staticResources("/", "") {
            extensions("html", "htm")
            enableAutoHeadResponse()
        }

        get("/listing") {
            call.respondHTMLResources("/listing.html")
        }
    }
}