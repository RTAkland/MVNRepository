/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo.routings

import cn.rtast.mvnrepo.*
import cn.rtast.mvnrepo.entity.MavenMetadata
import cn.rtast.mvnrepo.entity.PackageStructure
import cn.rtast.mvnrepo.util.str.fromXML
import cn.rtast.mvnrepo.util.str.toXMLString
import cn.rtast.mvnrepo.util.toMavenFormatedDate
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
                if (accountManager.validate(credentials.name, credentials.password)) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
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
                    serveFile(call)
                }
            }
        }

        REPOSITORIES.forEach {
            get(Regex("/$it/(.*)")) {
                serveFile(call)
            }
        }
    }
}

private suspend fun serveFile(call: ApplicationCall) {
    val file = File(STORAGE_PATH, call.request.uri)
    if (file.exists() && !file.isDirectory) {
        call.respondFile(file)
        calculateDownloadCount(call)
    } else {
        call.respond(HttpStatusCode.NotFound)
    }
}

private fun parseMetadata(call: ApplicationCall): Triple<String, String, String> {
    val uri = call.request.uri
    val repository = uri.split("/")[1]
    val packageGroup = uri.split("/")
        .drop(2).dropLast(3).joinToString("/")
    val artifactId = uri.split("/").dropLast(2).last()
    return Triple(repository, packageGroup, artifactId)
}

suspend fun calculateDownloadCount(call: ApplicationCall) {
    val (repository, packageGroup, artifactId) = parseMetadata(call)
    artifactManager.increaseDownloadCount(packageGroup, repository, artifactId)
}

suspend fun parsePUTPackage(call: ApplicationCall): PackageStructure {
    val fileBytes = call.receive<ByteArray>()
    val uri = call.request.uri
    val (repository, packageGroup, artifactId) = parseMetadata(call)
    val packageVersion = uri.split("/").dropLast(1).last()
    val jarName = uri.split("/").last()
    return PackageStructure(packageGroup, artifactId, packageVersion, jarName, repository, fileBytes)
}

@OptIn(ExperimentalSerializationApi::class)
suspend fun storagePackage(structure: PackageStructure, createBy: String) {
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
                Instant.now().epochSecond.toMavenFormatedDate()
            )
        )
        mavenMetadataFile.writeText(defaultMetadata.toXMLString())
    } else {
        val currentVersions = mavenMetadataFile.readText().fromXML<MavenMetadata>()
        val metadata = MavenMetadata(
            structure.artifactGroup.replace("/", "."),
            structure.artifactId,
            MavenMetadata.Versioning(
                structure.artifactVersion,
                structure.artifactVersion,
                MavenMetadata.Versions(
                    currentVersions.versioning.versions.version.toMutableList().apply {
                        add(structure.artifactVersion)
                    }.distinct()
                ),
                Instant.now().epochSecond.toMavenFormatedDate()
            )
        )
        mavenMetadataFile.writeText(metadata.toXMLString())
    }
    if (structure.artifactName.endsWith(".jar")) {
        artifactManager.addOrUpdateArtifact(structure, createBy)
    }
}