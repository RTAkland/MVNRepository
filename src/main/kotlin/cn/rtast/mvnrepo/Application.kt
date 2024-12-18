/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo

import cn.rtast.mvnrepo.routings.configureAPIRouting
import cn.rtast.mvnrepo.routings.configureFilesListing
import cn.rtast.mvnrepo.routings.configureRepositoryRouting
import cn.rtast.mvnrepo.util.AccountManager
import cn.rtast.mvnrepo.util.initDatabase
import io.ktor.server.application.*
import io.ktor.server.netty.*
import java.util.*

suspend fun main(args: Array<String>) {
    tempPassword = args.getOrElse(0) { UUID.randomUUID().toString() }
    println("你的密钥是: $tempPassword | 注意这个密钥不能用来发布包只能操作API")
    STORAGE_PATH.mkdirs()
    initDatabase()
    EngineMain.main(args)
}

fun Application.module() {
    configureRepositoryRouting()
    configureFilesListing()
    configureAPIRouting()
}

val accountManager = AccountManager()
var tempPassword = ""
