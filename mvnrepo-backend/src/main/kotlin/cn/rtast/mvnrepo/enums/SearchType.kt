/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/21
 */


package cn.rtast.mvnrepo.enums

enum class SearchType {
    ArtifactId, GroupId, Version, Repository, Author;

    companion object {
        fun fromString(value: String?): SearchType? {
            return when (value) {
                "artifactId" -> ArtifactId
                "groupId" -> GroupId
                "version" -> Version
                "repository" -> Repository
                "author" -> Author
                else -> null
            }
        }
    }
}