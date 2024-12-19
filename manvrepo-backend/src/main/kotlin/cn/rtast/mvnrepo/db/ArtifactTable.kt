/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/19
 */


package cn.rtast.mvnrepo.db

import org.jetbrains.exposed.sql.Table

object ArtifactTable : Table("artifacts") {
    val id = integer("id").autoIncrement()
    val groupId = varchar("group_id", 100)
    val artifactId = varchar("artifact_id", 100)
    val version = varchar("version", 50)
    val createAt = long("create_at")
    val createdBy = varchar("created_by", 50)
    val repository = varchar("repository", 50)

    override val primaryKey = PrimaryKey(id)
}