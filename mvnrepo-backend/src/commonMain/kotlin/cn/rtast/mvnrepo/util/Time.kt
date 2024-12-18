/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo.util

import kotlinx.datetime.Clock.System
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime


@OptIn(FormatStringsInDatetimeFormats::class)
fun Long.toFormatedDate(): String {
    val instant = Instant.fromEpochSeconds(this)
    val formatPattern = "yyyyMMddHHmmss"
    val dateTimeFormat = LocalDateTime.Format {
        byUnicodePattern(formatPattern)
    }
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return dateTimeFormat.format(localDateTime)
}