/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/19
 */


package cn.rtast.mvnrepo.routings.api

import cn.rtast.mvnrepo.PRIVATE_REPOSITORIES
import cn.rtast.mvnrepo.REPOSITORIES
import cn.rtast.mvnrepo.STORAGE_PATH
import cn.rtast.mvnrepo.artifactManager
import cn.rtast.mvnrepo.entity.api.APIArtifactSearchResult
import cn.rtast.mvnrepo.entity.api.DeleteArtifact
import cn.rtast.mvnrepo.entity.api.ListingFile
import cn.rtast.mvnrepo.entity.api.ResponseMessage
import cn.rtast.mvnrepo.enums.SearchType
import cn.rtast.mvnrepo.registry.listingFiles
import cn.rtast.mvnrepo.util.deleteDirectory
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
        authenticate("auth-jwt") {
            delete("/-/api/artifacts") {
                val artifact = call.receive<DeleteArtifact>()
                val filePath = File(
                    STORAGE_PATH,
                    "${artifact.repository}/${artifact.groupId.replace(".", "/")}/${artifact.artifactId}"
                )
                deleteDirectory(filePath)
                call.respond(ResponseMessage(200, "已删除: ${artifact.artifactId}"))
            }

            PRIVATE_REPOSITORIES.forEach {
                get(Regex("/-/api/artifacts/$it/(.*)")) {
                    call.listingFiles(it)
                }
            }

            get("/-/api/artifacts/search") {
                val keyword = call.parameters["keyword"]
                val type = call.parameters["type"]
                if (keyword == null) {
                    call.respondText(
                        ResponseMessage(404, "请添加`keyword`参数").toJson(),
                        status = HttpStatusCode.NotFound
                    )
                    return@get
                }
                val searchType = SearchType.fromString(type)
                if (searchType == null || type == null) {
                    call.respondText(
                        ResponseMessage(404, "请添加`type`参数").toJson(),
                        status = HttpStatusCode.NotFound
                    )
                    return@get
                }
                val result = artifactManager.searchByArtifact(keyword, searchType)
                val response = APIArtifactSearchResult(
                    "搜索到${result.size}个结果", 200, result.size, result
                ).toJson()
                call.respondText(response)
            }
        }

        REPOSITORIES.forEach {
            get(Regex("/-/api/artifacts/$it/(.*)")) {
                call.listingFiles(it)
            }
        }

        get("/-/api/artifacts") {
            call.respond(
                ListingFile(
                    "查询成功",
                    (REPOSITORIES + PRIVATE_REPOSITORIES).size,
                    (REPOSITORIES + PRIVATE_REPOSITORIES).map {
                        ListingFile.Files(it, true, 0)
                    })
            )
        }
    }
}