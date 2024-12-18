/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */

@file:Suppress("unused")

package cn.rtast.mvnrepo.util.str

import com.ryanharter.kotlinx.serialization.xml.Xml
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

@OptIn(ExperimentalSerializationApi::class)
fun Any.toXMLString(): String {
    return Xml.Default.encodeToString(this)
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> String.fromXML(): T {
    return Xml.Default.decodeFromString<T>(this)
}