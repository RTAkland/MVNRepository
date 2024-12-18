/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo.routings

import cn.rtast.mvnrepo.REPOSITORIES
import cn.rtast.mvnrepo.STORAGE_PATH
import cn.rtast.mvnrepo.entity.MavenMetadata
import cn.rtast.mvnrepo.entity.PackageStructure
import cn.rtast.mvnrepo.util.str.toXMLString
import cn.rtast.mvnrepo.util.toFormatedDate
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.File
import java.time.Instant

fun Application.configureRepositoryRouting() {
    install(AutoHeadResponse)
    install(ContentNegotiation)
    install(Authentication) {
        basic("authenticate") {
            validate { credentials ->
                if (credentials.name == credentials.password) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }

    routing {
        authenticate("authenticate") {
            REPOSITORIES.forEach {
                put(Regex("/$it/(.*)")) {
                    val packageStructure = parsePUTPackage(call)
                    storagePackage(packageStructure)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }

        REPOSITORIES.forEach {
            get(Regex("/$it/(.*)")) {
                val uri = call.request.uri
                val file = File(STORAGE_PATH, uri)
                call.respondFile(file)
            }
        }
    }
}

suspend fun parsePUTPackage(call: ApplicationCall): PackageStructure {
    val fileBytes = call.receive<ByteArray>()
    val uri = call.request.uri
    val repository = uri.split("/")[1]
    val packageGroup = uri.split("/")
        .drop(2).dropLast(3).joinToString("/")
    val artifactId = uri.split("/").dropLast(2).last()
    val packageVersion = uri.split("/").dropLast(1).last()
    val jarName = uri.split("/").last()
    return PackageStructure(packageGroup, artifactId, packageVersion, jarName, repository, fileBytes)
}

@OptIn(ExperimentalSerializationApi::class)
fun storagePackage(structure: PackageStructure) {
    val repositoryPath = File(STORAGE_PATH, structure.repository)
    val groupPath = File(repositoryPath, structure.artifactGroup)
    val artifactPath = File(groupPath, structure.artifactId)
    val versionPath = File(artifactPath, structure.artifactVersion).apply { mkdirs() }
    File(versionPath, structure.artifactName!!).apply { writeBytes(structure.artifactByteArray!!) }
    val mavenMetadataFile = File(artifactPath, "maven-metadata.xml")
    if (mavenMetadataFile.createNewFile()) {
        val defaultMetadata = MavenMetadata(
            structure.artifactGroup.replace("/", "."),
            structure.artifactId,
            MavenMetadata.Versioning(
                structure.artifactVersion,
                structure.artifactVersion,
                MavenMetadata.Versions(listOf(structure.artifactVersion)),
                Instant.now().epochSecond.toFormatedDate()
            )
        )
        mavenMetadataFile.writeText(defaultMetadata.toXMLString())
    }
}