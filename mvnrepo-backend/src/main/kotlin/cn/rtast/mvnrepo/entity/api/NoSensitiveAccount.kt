/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/25
 */


package cn.rtast.mvnrepo.entity.api

data class NoSensitiveAccount(
    val username: String,
    val creatAt: Long,
    val enabled: Boolean
)