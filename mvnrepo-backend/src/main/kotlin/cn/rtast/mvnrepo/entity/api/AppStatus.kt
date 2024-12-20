/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/20
 */


package cn.rtast.mvnrepo.entity.api

data class AppStatus(
    val jvmVersion: String,
    val totalMemory: Long,
    val freeMemory: Long,
    val maxMemory: Long,
    val kotlinVersion: String,
    val osName: String,
    val osArch: String,
    val osVersion: String,
)