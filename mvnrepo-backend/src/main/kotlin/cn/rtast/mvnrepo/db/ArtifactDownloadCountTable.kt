/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/19
 */


package cn.rtast.mvnrepo.db

import org.jetbrains.exposed.sql.Table

object ArtifactDownloadCountTable : Table("artifacts_count") {
    val id = integer("id").autoIncrement()
    val groupId = varchar("group_id", 128)
    val artifactId = varchar("artifact_id", 128)
    val repository = varchar("repository", 50)
    val downloadCount = long("download_count")

    override val primaryKey = PrimaryKey(id)
}