/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo.routings

import cn.rtast.mvnrepo.tempPassword
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.receiveText
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureAPIRouting() {
    routing {
        post("/-/api/user") {
            val header = call.request.headers["Authorization"] ?: ""
            println(header.toString())
            println(tempPassword)
            if (header == "Bearer $tempPassword") {
                val body = call.receiveText()
                call.respondText(body)
            } else {
                call.respond(HttpStatusCode.Forbidden)
            }
        }
    }
}