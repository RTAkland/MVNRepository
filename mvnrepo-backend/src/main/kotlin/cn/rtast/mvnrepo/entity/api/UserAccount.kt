/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/19
 */


package cn.rtast.mvnrepo.entity.api

data class UserAccount(
    val username: String,
    val enabled: Boolean,
    val password: String
)