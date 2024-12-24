/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/22
 */


package cn.rtast.mvnrepo.registry

import cn.rtast.mvnrepo.STORAGE_PATH
import cn.rtast.mvnrepo.artifactManager
import cn.rtast.mvnrepo.entity.MavenMetadata
import cn.rtast.mvnrepo.entity.PackageStructure
import cn.rtast.mvnrepo.entity.api.ListingFile
import cn.rtast.mvnrepo.util.str.fromXML
import cn.rtast.mvnrepo.util.str.toXMLString
import cn.rtast.mvnrepo.util.toMavenFormatedDate
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import java.io.File
import java.time.Instant

suspend fun serveMavenFiles(call: ApplicationCall) {
    val file = File(STORAGE_PATH, call.request.uri)
    if (file.exists() && !file.isDirectory) {
        call.respondFile(file)
        calculateDownloadCount(call)
    } else {
        call.respond(HttpStatusCode.NotFound)
    }
}

fun parseMetadata(call: ApplicationCall): Triple<String, String, String> {
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
    if (structure.artifactName.endsWith(".jar") || structure.artifactName.endsWith("klib")) {
        artifactManager.addOrUpdateArtifact(structure, createBy)
    }
}

suspend fun ApplicationCall.listingFiles(repo: String) {
    val path = this.request.uri.split("?").first().replace("/-/api/artifacts/$repo/", "/$repo/")
    val takeLimit = this.parameters["limit"]?.toInt() ?: 100
    val file = File(STORAGE_PATH, path)
    if (file.isFile) {
        this.respondFile(file)
    } else {
        val files = file.listFiles()?.asSequence()?.take(takeLimit)?.toList()?.map {
            ListingFile.Files(it.name, it.isDirectory, it.length())
        } ?: emptyList()
        val listingFile = ListingFile("查询成功", files.size, files)
        this.respond(listingFile)
    }
}