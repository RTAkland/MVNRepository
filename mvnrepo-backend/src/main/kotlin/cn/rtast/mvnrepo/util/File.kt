/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/19
 */


package cn.rtast.mvnrepo.util

import cn.rtast.mvnrepo.STORAGE_PATH
import cn.rtast.mvnrepo.entity.api.ListingFile
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File
import java.net.URI


fun deleteDirectory(file: File): Boolean {
    if (file.isDirectory) {
        val files = file.listFiles()
        if (files != null) {
            for (subFile in files) {
                deleteDirectory(subFile)
            }
        }
    }
    return file.delete()
}

suspend fun ApplicationCall.respondHTMLResources(path: String) {
    val resourcesFileText = String(javaClass.getResourceAsStream(path)!!.readBytes())
    this.respondText(contentType = ContentType.Text.Html, text = resourcesFileText)
}

object S3Storage {

    val s3Client = this.createS3Client()

    private fun createS3Client(): S3Client {
        val credentials = AwsBasicCredentials.create(
            "x",
            "x"
        )
        return S3Client.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .endpointOverride(URI.create("https://x.r2.cloudflarestorage.com"))
            .build()
    }

    fun storageFile(file: File, content: ByteArray) {
        val putObjectRequest = PutObjectRequest.builder()
            .bucket("mvnrepo")
            .contentType(file.toMimeType())
            .key(file.path.replace("\\", "/").split("/").dropLast(1).joinToString("/") + "/" + file.name)
            .build()
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(content))
    }

    fun listFile(path: String): ListingFile {
        try {
            val request = ListObjectsV2Request.builder()
                .bucket("mvnrepo")
                .prefix("$path/")
                .delimiter("/")
                .build()
            val response = s3Client.listObjectsV2(request)
            val files = response.contents().map {
                ListingFile.Files(it.key().split("/").last(), false, it.size())
            }
            val dirs = response.commonPrefixes().map {
                ListingFile.Files(it.prefix(), true, 0)
            }
            return ListingFile("查询成功", files.size + dirs.size, files + dirs)
        } catch (_: Exception) {
            return ListingFile("查询成功", 0, emptyList())
        }
    }

    fun deleteFile(file: File) {
        val request = DeleteObjectRequest.builder()
            .bucket("mvnrepo")
            .key(file.path.replace("\\", "/").split("/").dropLast(1).joinToString("/") + "/" + file.name)
            .build()
        s3Client.deleteObject(request)
    }
}


fun main() {
    S3Storage.storageFile(
        File("repository/releases/config.json"),
        File(STORAGE_PATH, "config.json").readBytes(),
    )
    println(S3Storage.listFile("repository/releases"))
    S3Storage.deleteFile(File("repository/releases/config.json"))
}