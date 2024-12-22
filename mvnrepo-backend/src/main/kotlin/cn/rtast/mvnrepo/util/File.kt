/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/19
 */


package cn.rtast.mvnrepo.util

import io.ktor.http.ContentType
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respondText
import java.io.File


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