/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo.routings.api

import cn.rtast.mvnrepo.JWT_SECRET
import cn.rtast.mvnrepo.accountManager
import cn.rtast.mvnrepo.entity.api.*
import cn.rtast.mvnrepo.security.generateJWT
import cn.rtast.mvnrepo.util.str.toJson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureUserAPIRouting() {
    routing {
        post("/-/api/login") {
            val user = call.receive<UserAccount>()
            if (accountManager.validate(user.username, user.password)) {
                val token = generateJWT(user.username, JWT_SECRET)
                call.respond(JWTResponse(token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "账号或密码错误")
            }
        }

        authenticate("auth-jwt") {
            get("/-/api/user") {
                val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString()!!
                val account = accountManager.getAccount(username)!!
                call.respond(NoSensitiveAccount(account.username, account.createAt, account.enabled))
            }

            post("/-/api/user") {
                val account = call.receive<UserAccount>()
                if (accountManager.getAccount(account.username) == null) {
                    accountManager.addAccount(account.username, account.password)
                    call.respondText(ResponseMessage(201, "账户创建成功").toJson(), status = HttpStatusCode.Created)
                } else {
                    call.respondText(
                        ResponseMessage(409, "账户已存在").toJson(),
                        status = HttpStatusCode.Conflict
                    )
                }
            }

            put("/-/api/user") {
                val account = call.receive<UserAccount>()
                if (accountManager.getAccount(account.username) == null) {
                    call.respondText(
                        ResponseMessage(404, "账户不存在, 使用POST方法提交相同的内容来创建账户").toJson(),
                        status = HttpStatusCode.NotFound
                    )
                } else {
                    accountManager.updateAccount(account.username, account.password, account.enabled)
                    call.respond(ResponseMessage(200, "账户更新成功"))
                }
            }

            delete("/-/api/user") {
                val account = call.receive<DeleteAccount>()
                if (accountManager.getAccount(account.username) == null) {
                    call.respondText(
                        ResponseMessage(404, "账户不存在").toJson(),
                        status = HttpStatusCode.NotFound
                    )
                } else {
                    accountManager.deleteAccount(account.username)
                    call.respond(ResponseMessage(200, "账户删除成功"))
                }
            }
        }
    }
}