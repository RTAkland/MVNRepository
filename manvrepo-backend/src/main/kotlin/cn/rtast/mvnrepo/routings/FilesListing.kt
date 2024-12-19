/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo.routings

import cn.rtast.mvnrepo.REPOSITORIES
import cn.rtast.mvnrepo.STORAGE_PATH
import cn.rtast.mvnrepo.entity.FileListing
import com.github.mustachejava.DefaultMustacheFactory
import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.webjars.*
import java.io.File

fun Application.configureFilesListing() {
    install(Webjars) {
        path = "assets"
    }
    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("templates")
    }

    routing {
        REPOSITORIES.forEach { repo ->
            get(Regex("/$repo/(.*)")) {
                val uri = call.request.uri
                val filePath = File(STORAGE_PATH, uri)
                if (filePath.exists() && filePath.isDirectory) {
                    val files = filePath.listFiles()?.map {
                        FileListing(
                            path = "$uri/${it.name}",
                            filename = it.name,
                            isDirectory = it.isDirectory
                        )
                    } ?: emptyList()
                    call.respond(MustacheContent("file_listing.hbs", mapOf("path" to uri, "files" to files)))
                } else if (filePath.exists() && filePath.isFile) {
                    call.respondFile(filePath)
                } else {
                    call.respond("File not found")
                }
            }
        }
    }
}