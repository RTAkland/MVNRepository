/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/19
 */


package cn.rtast.mvnrepo.routings

import cn.rtast.mvnrepo.STORAGE_PATH
import cn.rtast.mvnrepo.entity.api.DeleteArtifact
import cn.rtast.mvnrepo.entity.api.ResponseMessage
import cn.rtast.mvnrepo.util.deleteDirectory
import cn.rtast.mvnrepo.util.str.fromJson
import cn.rtast.mvnrepo.util.str.toJson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRepositoryAPIRouting() {
    routing {
        authenticate("authenticate") {
            delete("/-/api/artifact") {
                val artifact = call.receiveText().fromJson<DeleteArtifact>()
                val filePath = File(
                    STORAGE_PATH,
                    "${artifact.repository}/${artifact.groupId.replace(".", "/")}/${artifact.artifactId}"
                )
                deleteDirectory(filePath)
                call.respondText(ResponseMessage(200, "已删除: ${artifact.artifactId}").toJson())
            }
        }
    }
}