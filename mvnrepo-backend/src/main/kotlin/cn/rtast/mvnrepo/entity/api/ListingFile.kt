/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/19
 */


package cn.rtast.mvnrepo.entity.api

data class ListingFile(
    val data: List<Files>
) {
    data class Files(
        val name: String,
        val idDirectory: Boolean
    )
}