/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/19
 */


package cn.rtast.mvnrepo.entity.api

data class ListingFile(
    val message: String,
    val count: Int,
    val data: List<Files>,
) {
    data class Files(
        val name: String,
        val isDirectory: Boolean,
        val size: Long,
    )
}