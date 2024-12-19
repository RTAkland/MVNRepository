/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/19
 */


package cn.rtast.mvnrepo.entity.api

import java.time.Instant

data class NoSensitiveAccount(
    val message: String,
    val code: Int,
    val data: Account,
    val timestamp: Long = Instant.now().epochSecond
) {
    data class Account(
        val username: String,
        val createAt: String,
        val enabled: Boolean
    )
}