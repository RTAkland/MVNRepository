/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo

import cn.rtast.mvnrepo.routings.api.configureRepositoryAPIRouting
import cn.rtast.mvnrepo.routings.api.configureUserAPIRouting
import cn.rtast.mvnrepo.routings.configureIndexRouting
import cn.rtast.mvnrepo.routings.configureRepositoryRouting
import cn.rtast.mvnrepo.util.AccountManager
import cn.rtast.mvnrepo.util.initDatabase
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import java.util.*

suspend fun main(args: Array<String>) {
    val parser = ArgParser("kwsify-cli")
    val port by parser.option(ArgType.Int, shortName = "p", description = "Port number").default(8088)
    val tempToken by parser.option(ArgType.String, shortName = "t", description = "Temp token")
        .default("${UUID.randomUUID()}")
    parser.parse(args)
    STORAGE_PATH.mkdirs()
    initializeDatabase(tempToken)
    embeddedServer(
        Netty, port = port,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureRepositoryRouting()
    configureRepositoryAPIRouting()
    configureUserAPIRouting()
    configureIndexRouting()
}

suspend fun initializeDatabase(token: String) {
    initDatabase()
    if (accountManager.initAdminAccount(token)) {
        println("你的默认账号是: admin")
        println("你的默认密码: $token")
        println("已将其写入数据库下次启动无需在使用-t参数")
    } else {
        val adminAccount = accountManager.getAccount("admin")
        println("账号: admin")
        println("密码: ${adminAccount?.password}")
    }
}

val accountManager = AccountManager()
