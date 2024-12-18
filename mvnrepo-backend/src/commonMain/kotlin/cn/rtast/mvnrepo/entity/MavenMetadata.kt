/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo.entity


@Xml
data class MavenMetadata(
    val groupId: String,
    val artifactId: String,
    val versioning: Versioning,
) {
    data class Versioning(
        val latest: String,
        val release: String,
        val versions: Versions,
        val lastUpdated: String,
    )

    data class Versions(
        val version: List<String>,
    )
}