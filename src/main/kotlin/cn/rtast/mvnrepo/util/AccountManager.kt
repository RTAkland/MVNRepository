/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo.util

import cn.rtast.mvnrepo.db.AccountTable
import cn.rtast.mvnrepo.entity.AccountEntity
import org.jetbrains.exposed.sql.selectAll

class AccountManager {
    suspend fun validate(username: String, password: String): Boolean {
        return suspendedTransaction {
            AccountTable.selectAll()
                .map {
                    AccountEntity(
                        it[AccountTable.username],
                        it[AccountTable.password],
                        it[AccountTable.createAt],
                        it[AccountTable.enabled]
                    )
                }.find { it.username == username && it.password == password && it.enabled == true }?.let {
                    return@suspendedTransaction true
                }
            return@suspendedTransaction false
        }
    }
}