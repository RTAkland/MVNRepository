/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/20
 */


package cn.rtast.mvnrepo.entity.api

data class PackageStatistics(
    val group: String,
    val artifactId: String,
    val repository: String,
    val downloadCount: Long
)