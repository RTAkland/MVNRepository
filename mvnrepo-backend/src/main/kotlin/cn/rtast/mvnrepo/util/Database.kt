/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo.util

import cn.rtast.mvnrepo.STORAGE_PATH
import cn.rtast.mvnrepo.db.AccountTable
import cn.rtast.mvnrepo.db.ArtifactDownloadCountTable
import cn.rtast.mvnrepo.db.ArtifactTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> suspendedTransaction(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }

suspend fun initDatabase() {
    Database.connect("jdbc:sqlite:./$STORAGE_PATH/data.sqlite?journal_mode=WAL", driver = "org.sqlite.JDBC")
    suspendedTransaction {
        SchemaUtils.createMissingTablesAndColumns(AccountTable)
        SchemaUtils.createMissingTablesAndColumns(ArtifactDownloadCountTable)
        SchemaUtils.createMissingTablesAndColumns(ArtifactTable)
    }
}