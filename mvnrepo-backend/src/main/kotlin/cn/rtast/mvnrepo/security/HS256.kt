/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/22
 */


package cn.rtast.mvnrepo.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

fun generateJWT(username: String, secretKey: String): String {
    return JWT.create()
        .withAudience("mvnrepo")
        .withExpiresAt(Date(System.currentTimeMillis() + 7200 * 1000))
        .withClaim("username", username)
        .sign(Algorithm.HMAC256(secretKey))
}