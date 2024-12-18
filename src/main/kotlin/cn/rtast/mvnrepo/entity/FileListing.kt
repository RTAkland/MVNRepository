/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo.entity

data class FileListing(
    val path: String,
    val filename: String,
    val isDirectory: Boolean
)