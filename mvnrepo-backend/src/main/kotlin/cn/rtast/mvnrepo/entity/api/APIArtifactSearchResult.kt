/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/20
 */


package cn.rtast.mvnrepo.entity.api

import cn.rtast.mvnrepo.entity.db.ArtifactSearchResult

data class APIArtifactSearchResult(
    val message: String,
    val code: Int,
    val count: Int,
    val data: List<ArtifactSearchResult>
)