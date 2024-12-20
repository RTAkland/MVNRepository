/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/20
 */


package cn.rtast.mvnrepo.routings.api

import cn.rtast.mvnrepo.artifactManager
import cn.rtast.mvnrepo.entity.api.PackageStatistics
import cn.rtast.mvnrepo.entity.api.ResponseMessage
import cn.rtast.mvnrepo.util.str.toJson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configurePackageStatisticsAPIRouting() {
    routing {
        authenticate("authenticate") {
            get("/-/api/statistics") {
                val groupId =(call.parameters["groupId"] ?: call.parameters["group"])?.replace(".", "/")
                val repository = call.parameters["repository"] ?: call.parameters["repo"]
                val artifactId = call.parameters["artifactId"] ?: call.parameters["artifact"]
                if (groupId == null || repository == null || artifactId == null) {
                    call.respondText(
                        ResponseMessage(404, "未查询到该构件的统计数据").toJson(),
                        status = HttpStatusCode.NotFound
                    )
                } else {
                    val result = artifactManager.getDownloadCount(groupId, repository, artifactId)
                    if (result == null) {
                        call.respondText(
                            ResponseMessage(404, "未查询到该构件的统计数据").toJson(),
                            status = HttpStatusCode.NotFound
                        )
                    } else {
                        call.respondText(
                            PackageStatistics(
                                result.group,
                                result.artifactId,
                                result.repository,
                                result.downloadCount
                            ).toJson()
                        )
                    }
                }
            }
        }
    }
}