/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo.entity

data class AccountEntity(
    val username: String,
    val password: String,
    val createAt: Long,
    val enabled: Boolean,
)