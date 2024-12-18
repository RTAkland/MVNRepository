/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo.entity

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement


@JacksonXmlRootElement(localName = "metadata")
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
        @JacksonXmlElementWrapper(useWrapping = false)
        val version: List<String>,
    )
}