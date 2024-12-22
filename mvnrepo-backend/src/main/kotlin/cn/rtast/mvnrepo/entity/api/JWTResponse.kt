/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/22
 */


package cn.rtast.mvnrepo.entity.api

import com.google.gson.annotations.SerializedName

data class JWTResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("expires_in")
    val expiresIn: Long = 7200
)