/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/27
 */


package cn.rtast.mvnrepo.util

import java.io.File

fun File.toMimeType(): String? {
    return when (this.extension) {
        "jar" -> "application/java-archive"
        "pom", "xml" -> "application/xml"
        else -> null
    }
}