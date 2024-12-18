/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/18
 */


package cn.rtast.mvnrepo.entity

data class PackageStructure(
    val artifactGroup: String,
    val artifactId: String,
    val artifactVersion: String,
    val artifactName: String?,
    val repository: String,
    val artifactByteArray: ByteArray?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PackageStructure

        if (artifactGroup != other.artifactGroup) return false
        if (artifactVersion != other.artifactVersion) return false
        if (artifactName != other.artifactName) return false
        if (repository != other.repository) return false
        if (!artifactByteArray.contentEquals(other.artifactByteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = artifactGroup.hashCode()
        result = 31 * result + artifactVersion.hashCode()
        result = 31 * result + artifactName.hashCode()
        result = 31 * result + repository.hashCode()
        result = 31 * result + artifactByteArray.contentHashCode()
        return result
    }
}