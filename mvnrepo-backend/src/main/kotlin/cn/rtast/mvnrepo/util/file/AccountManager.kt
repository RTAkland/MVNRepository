/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo.util.file

import cn.rtast.mvnrepo.db.AccountTable
import cn.rtast.mvnrepo.entity.AccountEntity
import cn.rtast.mvnrepo.util.suspendedTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant

class AccountManager {

    suspend fun updateAccount(username: String, password: String, enabled: Boolean) {
        suspendedTransaction {
            AccountTable.update({ AccountTable.username eq username }) {
                it[AccountTable.password] = password
                it[AccountTable.enabled] = enabled
                it[AccountTable.username] = username
            }
        }
    }

    suspend fun addAccount(username: String, password: String) {
        suspendedTransaction {
            AccountTable.insert {
                it[AccountTable.username] = username
                it[AccountTable.password] = password
                it[createAt] = Instant.now().epochSecond
                it[enabled] = true
            }
        }
    }

    suspend fun initAdminAccount(defaultPassword: String): Boolean {
        return suspendedTransaction {
            val ifAdminExists = AccountTable.selectAll().where { AccountTable.username eq "admin" }.firstOrNull()
            if (ifAdminExists == null) {
                addAccount("admin", defaultPassword)
                return@suspendedTransaction true
            }
            return@suspendedTransaction false
        }
    }

    suspend fun getAccount(username: String): AccountEntity? {
        return suspendedTransaction {
            AccountTable.selectAll()
                .map {
                    AccountEntity(
                        it[AccountTable.username],
                        it[AccountTable.password],
                        it[AccountTable.createAt],
                        it[AccountTable.enabled]
                    )
                }.find { it.username == username }
        }
    }

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

    suspend fun deleteAccount(username: String) {
        suspendedTransaction {
            AccountTable.deleteWhere { AccountTable.username eq username }
        }
    }
}