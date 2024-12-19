/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/19
 */


package cn.rtast.mvnrepo.entity.api

import java.time.Instant

data class ResponseMessage(
    val code: Int,
    val message: String,
    val timestamp: Long = Instant.now().epochSecond
)