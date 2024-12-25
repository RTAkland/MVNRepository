/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */

@file:Suppress("unused")

package cn.rtast.mvnrepo

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.xml.XmlFactory
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.time.Instant
import java.util.UUID

val gson: Gson = GsonBuilder()
    .disableHtmlEscaping()
    .create()

const val STORAGE_PATH_ = "repository"
val STORAGE_PATH = File(STORAGE_PATH_)
val REPOSITORIES = listOf(
    "releases",
    "snapshots",
)
val PRIVATE_REPOSITORIES = listOf(
    "private"
)

val xmlMapper = XmlMapper(XmlFactory().apply {
    enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
}).registerKotlinModule().apply {
    enable(SerializationFeature.INDENT_OUTPUT)
    registerModule(SimpleModule())
}

val JWT_SECRET = UUID.randomUUID().toString()

val STARTUP_TIME = Instant.now().epochSecond