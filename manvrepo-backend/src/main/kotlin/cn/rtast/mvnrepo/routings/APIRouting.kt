/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo.routings

import cn.rtast.mvnrepo.accountManager
import cn.rtast.mvnrepo.entity.api.PostAddAccount
import cn.rtast.mvnrepo.entity.api.ResponseMessage
import cn.rtast.mvnrepo.util.str.fromJson
import cn.rtast.mvnrepo.util.str.toJson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureAPIRouting() {
    routing {
        authenticate("authenticate") {
            post("/-/api/user") {
                val account = call.receiveText().fromJson<PostAddAccount>()
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
                val account = call.receiveText().fromJson<PostAddAccount>()
                if (accountManager.getAccount(account.username) == null) {
                    call.respondText(
                        ResponseMessage(404, "账户不存在, 使用POST方法提交相同的内容来创建账户").toJson(),
                        status = HttpStatusCode.NotFound
                    )
                } else {
                    accountManager.updateAccount(account.username, account.password)
                    call.respondText(ResponseMessage(200, "账户更新成功").toJson())
                }
            }
        }
    }
}