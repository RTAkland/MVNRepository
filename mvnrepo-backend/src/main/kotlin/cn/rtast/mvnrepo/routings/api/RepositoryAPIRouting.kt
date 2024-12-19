/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/19
 */


package cn.rtast.mvnrepo.routings.api

import cn.rtast.mvnrepo.REPOSITORIES
import cn.rtast.mvnrepo.STORAGE_PATH
import cn.rtast.mvnrepo.entity.api.DeleteArtifact
import cn.rtast.mvnrepo.entity.api.ListingFile
import cn.rtast.mvnrepo.entity.api.ResponseMessage
import cn.rtast.mvnrepo.util.deleteDirectory
import cn.rtast.mvnrepo.util.str.fromJson
import cn.rtast.mvnrepo.util.str.toJson
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRepositoryAPIRouting() {
    routing {
        authenticate("authenticate") {
            delete("/-/api/artifacts") {
                val artifact = call.receiveText().fromJson<DeleteArtifact>()
                val filePath = File(
                    STORAGE_PATH,
                    "${artifact.repository}/${artifact.groupId.replace(".", "/")}/${artifact.artifactId}"
                )
                deleteDirectory(filePath)
                call.respondText(ResponseMessage(200, "已删除: ${artifact.artifactId}").toJson())
            }
        }

        get(Regex("/-/api/artifacts/(.*)")) {
            val path = call.request.uri.replace("/-/api/artifacts/", "/")
            if (path == "/") {
                call.respondText(ListingFile(REPOSITORIES.map { ListingFile.Files(it, true) }).toJson())
            } else {
                val file = File(STORAGE_PATH, path)
                if (file.isFile) {
                    call.respondFile(file)
                } else {
                    val files = file.listFiles()?.map {
                        ListingFile.Files(it.name, it.isDirectory)
                    }
                    if (files == null) {
                        call.respond(HttpStatusCode.NotFound)
                    } else {
                        val listingFile = ListingFile(files)
                        call.respondText(listingFile.toJson())
                    }
                }
            }
        }
    }
}