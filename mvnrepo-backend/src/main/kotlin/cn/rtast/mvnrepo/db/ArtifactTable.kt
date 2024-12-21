/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/20
 */


package cn.rtast.mvnrepo.db

import org.jetbrains.exposed.sql.Table

object ArtifactTable : Table("artifacts") {
    val id = integer("id").autoIncrement()
    val groupId = varchar("group_id", 128)
    val artifactId = varchar("artifact_id", 128)
    val repository = varchar("repository", 50)
    val version = varchar("version", 50)
    val createdBy = varchar("created_by", 50)
    val lastUpdated = long("last_updated")

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(groupId, artifactId, repository, version)
    }
}