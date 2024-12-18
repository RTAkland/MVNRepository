/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo

import cn.rtast.mvnrepo.routings.configureRepositoryRouting
import cn.rtast.mvnrepo.util.AccountManager
import cn.rtast.mvnrepo.util.initDatabase
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

suspend fun main(args: Array<String>) {
    STORAGE_PATH.mkdirs()
    initDatabase()
    EngineMain.main(args)
}

fun Application.module() {
    configureRepositoryRouting()
}

val accountManager = AccountManager()
