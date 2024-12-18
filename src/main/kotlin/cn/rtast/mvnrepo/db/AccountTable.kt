/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo.db

import org.jetbrains.exposed.sql.Table

object AccountTable : Table("account") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50)
    val password = varchar("password", 50)
    val enabled = bool("enabled")
    val createAt = long("create_at")

    override val primaryKey = PrimaryKey(id)
}