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
            delete("/-/api/artifacts") {
                val artifact = call.receiveText().fromJson<DeleteArtifact>()
                val filePath = File(
                    STORAGE_PATH,
                    "${artifact.repository}/${artifact.groupId.replace(".", "/")}/${artifact.artifactId}"
                )
                deleteDirectory(filePath)
                call.respondText(ResponseMessage(200, "已删除: ${artifact.artifactId}").toJson())
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
            call.respondText(
                ListingFile(
                    "查询成功",
                    (REPOSITORIES + PRIVATE_REPOSITORIES).size,
                    (REPOSITORIES + PRIVATE_REPOSITORIES).map {
                        ListingFile.Files(it, true)
                    }).toJson()
            )
        }
    }
}

private suspend fun ApplicationCall.listingFiles(repo: String) {
    val path = this.request.uri.split("?").first().replace("/-/api/artifacts/$repo/", "/$repo/")
    val takeLimit = this.parameters["limit"]?.toInt() ?: 100
    val file = File(STORAGE_PATH, path)
    if (file.isFile) {
        this.respondFile(file)
    } else {
        val files = file.listFiles()?.asSequence()?.take(takeLimit)?.toList()?.map {
            ListingFile.Files(it.name, it.isDirectory)
        } ?: emptyList()
        val listingFile = ListingFile("查询成功", files.size, files)
        this.respondText(listingFile.toJson())
    }
}