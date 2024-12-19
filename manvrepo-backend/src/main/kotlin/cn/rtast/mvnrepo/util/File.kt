/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/19
 */


package cn.rtast.mvnrepo.util

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