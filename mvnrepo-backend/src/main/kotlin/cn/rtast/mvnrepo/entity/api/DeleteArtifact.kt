/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/19
 */


package cn.rtast.mvnrepo.entity.api

data class DeleteArtifact(
    val artifactId: String,
    val groupId: String,
    val version: String,
    val repository: String,
)