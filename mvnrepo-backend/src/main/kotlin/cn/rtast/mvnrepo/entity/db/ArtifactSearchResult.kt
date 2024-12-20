/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/20
 */


package cn.rtast.mvnrepo.entity.db

data class ArtifactSearchResult(
    val groupId: String,
    val artifactId: String,
    val repository: String,
    val createdBy: String,
    val lastUpdated: Long,
    val version: String,
)