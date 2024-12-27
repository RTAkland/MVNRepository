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
import cn.rtast.mvnrepo.enums.StorageType
import cn.rtast.mvnrepo.util.S3Storage
import cn.rtast.mvnrepo.util.str.fromXML
import cn.rtast.mvnrepo.util.str.toXMLString
import cn.rtast.mvnrepo.util.toMavenFormatedDate
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import software.amazon.awssdk.services.s3.model.GetObjectRequest
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

suspend fun storagePackage(structure: PackageStructure, createBy: String, storageType: StorageType) {
    val repositoryPath = File(STORAGE_PATH, structure.repository)
    val groupPath = File(repositoryPath, structure.artifactGroup)
    val artifactPath = File(groupPath, structure.artifactId)
    val versionPath = File(artifactPath, structure.artifactVersion).apply { mkdirs() }

    when (storageType) {
        StorageType.S3 -> {
            structure.artifactName?.let { artifactName ->
                structure.artifactByteArray?.let { artifactByteArray ->
                    val file = File(versionPath, artifactName)
                    S3Storage.storageFile(file, artifactByteArray)
                    updateMavenMetadataS3(artifactPath, structure)
                } ?: throw IllegalArgumentException("Artifact byte array cannot be null")
            } ?: throw IllegalArgumentException("Artifact name cannot be null")
        }
        StorageType.LocalFile -> {
            structure.artifactName?.let { artifactName ->
                structure.artifactByteArray?.let { artifactByteArray ->
                    File(versionPath, artifactName).writeBytes(artifactByteArray)
                    updateMavenMetadataLocal(artifactPath, structure)
                } ?: throw IllegalArgumentException("Artifact byte array cannot be null")
            } ?: throw IllegalArgumentException("Artifact name cannot be null")
        }
    }

    if (structure.artifactName.endsWith(".jar") == true || structure.artifactName.endsWith("klib") == true) {
        artifactManager.addOrUpdateArtifact(structure, createBy)
    }
}

fun updateMavenMetadataLocal(artifactPath: File, structure: PackageStructure) {
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
}

fun updateMavenMetadataS3(artifactPath: File, structure: PackageStructure) {
    val metadataKey = "$artifactPath/maven-metadata.xml"
    try {
        val s3MetadataFile = S3Storage.s3Client.getObject(GetObjectRequest.builder()
            .bucket("mvnrepo")
            .key(metadataKey)
            .build())
        val currentMetadata = s3MetadataFile.readAllBytes().toString(Charsets.UTF_8).fromXML<MavenMetadata>()
        val metadata = MavenMetadata(
            structure.artifactGroup.replace("/", "."),
            structure.artifactId,
            MavenMetadata.Versioning(
                structure.artifactVersion,
                structure.artifactVersion,
                MavenMetadata.Versions(
                    currentMetadata.versioning.versions.version.toMutableList().apply {
                        add(structure.artifactVersion)
                    }.distinct()
                ),
                Instant.now().epochSecond.toMavenFormatedDate()
            )
        )
        S3Storage.storageFile(File(artifactPath, "maven-metadata.xml"), metadata.toXMLString().toByteArray())
    } catch (_: Exception) {
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
        S3Storage.storageFile(File(artifactPath, "maven-metadata.xml"), defaultMetadata.toXMLString().toByteArray())
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