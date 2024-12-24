/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo.routings

import cn.rtast.mvnrepo.JWT_SECRET
import cn.rtast.mvnrepo.PRIVATE_REPOSITORIES
import cn.rtast.mvnrepo.REPOSITORIES
import cn.rtast.mvnrepo.accountManager
import cn.rtast.mvnrepo.registry.parsePUTPackage
import cn.rtast.mvnrepo.registry.serveMavenFiles
import cn.rtast.mvnrepo.registry.storagePackage
import cn.rtast.mvnrepo.registry.storagePypiArtifact
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureMavenRepositoryRouting() {
    install(AutoHeadResponse)
    install(ContentNegotiation) {
        gson()
    }
    install(Authentication) {
        basic("authenticate") {
            validate { credentials ->
                if (accountManager.validate(credentials.name, credentials.password)) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
        jwt("auth-jwt") {
            verifier(JWT.require(Algorithm.HMAC256(JWT_SECRET)).withAudience("mvnrepo").build())
            validate { credential ->
                if (credential.payload.audience.contains("mvnrepo")) JWTPrincipal(credential.payload) else null
            }
        }
    }

    routing {
        authenticate("authenticate") {
            (REPOSITORIES + PRIVATE_REPOSITORIES).forEach {
                put(Regex("/$it/(.*)")) {
                    val authedUser = call.principal<UserIdPrincipal>()?.name!!
                    val packageStructure = parsePUTPackage(call)
                    storagePackage(packageStructure, authedUser)
                    call.respond(HttpStatusCode.OK)
                }
            }

            PRIVATE_REPOSITORIES.forEach {
                get(Regex("/$it/(.*)")) {
                    serveMavenFiles(call)
                }
            }
        }

        REPOSITORIES.forEach {
            get(Regex("/$it/(.*)")) {
                serveMavenFiles(call)
            }
        }
    }
}

fun Application.configurePypiRepositoryRouting() {
    routing {
        post("/simple") {
            storagePypiArtifact(call.receiveMultipart())
        }
    }
}