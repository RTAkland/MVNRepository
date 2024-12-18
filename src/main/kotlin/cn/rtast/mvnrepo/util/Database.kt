/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo.util

import cn.rtast.mvnrepo.STORAGE_PATH
import cn.rtast.mvnrepo.db.AccountTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> suspendedTransaction(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }

suspend fun initDatabase() {
    Database.connect("jdbc:h2:file:./$STORAGE_PATH/data.h2;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
    suspendedTransaction {
        SchemaUtils.createMissingTablesAndColumns(AccountTable)
    }
}