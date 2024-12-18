/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    .withZone(ZoneId.systemDefault())

fun Long.toFormatedDate(): String {
    val instant = Instant.ofEpochSecond(this)
    return formatter.format(instant)
}