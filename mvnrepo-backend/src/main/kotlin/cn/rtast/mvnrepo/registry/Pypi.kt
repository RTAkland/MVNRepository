/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/24
 */


package cn.rtast.mvnrepo.registry

import cn.rtast.mvnrepo.STORAGE_PATH
import io.ktor.http.content.*
import io.ktor.utils.io.*
import kotlinx.io.readByteArray
import java.io.File

suspend fun storagePypiArtifact(multipart: MultiPartData) {
    val pypiPackagePath = File(STORAGE_PATH, "pypi/simple")
    var packageName = ""
    multipart.forEachPart { part ->
        when (part) {
            is PartData.BinaryChannelItem -> TODO()
            is PartData.BinaryItem -> TODO()
            is PartData.FileItem -> {
                packageName = part.originalFileName ?: return@forEachPart
                File(pypiPackagePath, part.originalFileName ?: return@forEachPart)
                    .apply { writeBytes(part.provider().readRemaining().readByteArray()) }
            }

            is PartData.FormItem -> {
                println("form: ${part.name} -> ${part.value}")
            }
        }
    }
}