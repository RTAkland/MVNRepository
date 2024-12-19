/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val mavenFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    .withZone(ZoneId.systemDefault())
private val normalFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    .withZone(ZoneId.systemDefault())

fun Long.toMavenFormatedDate(): String {
    val instant = Instant.ofEpochSecond(this)
    return mavenFormatter.format(instant)
}

fun Long.toFormattedDate(): String {
    val instant = Instant.ofEpochSecond(this)
    return normalFormatter.format(instant)
}