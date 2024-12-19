/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */

@file:Suppress("unused")

package cn.rtast.mvnrepo.util.str

import cn.rtast.mvnrepo.xmlMapper

fun Any.toXMLString(): String {
    return xmlMapper.writeValueAsString(this)
}

inline fun <reified T> String.fromXML(): T {
    return xmlMapper.readValue(this, T::class.java)
}