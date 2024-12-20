/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/19
 */


package cn.rtast.mvnrepo.util.file

import cn.rtast.mvnrepo.db.ArtifactTable
import cn.rtast.mvnrepo.entity.PackageStructure
import cn.rtast.mvnrepo.entity.api.DeleteArtifact
import cn.rtast.mvnrepo.util.suspendedTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant

class ArtifactManager {
    suspend fun addArtifact(structure: PackageStructure, createUser: String) {
        suspendedTransaction {
            val ifArtifactExists = ArtifactTable.selectAll().where {
                (ArtifactTable.groupId eq structure.artifactGroup)
                    .and(ArtifactTable.artifactId eq structure.artifactId)
                    .and(ArtifactTable.version eq structure.artifactVersion)
                    .and(ArtifactTable.repository eq structure.repository)
            }.distinct().firstOrNull()
            if (ifArtifactExists == null) {
                ArtifactTable.insert {
                    it[groupId] = structure.artifactGroup
                    it[artifactId] = structure.artifactId
                    it[version] = structure.artifactVersion
                    it[createAt] = Instant.now().epochSecond
                    it[createdBy] = createUser
                    it[repository] = structure.repository
                }
            } else {
                ArtifactTable.update {
                    it[groupId] = structure.artifactGroup
                    it[artifactId] = structure.artifactId
                    it[version] = structure.artifactVersion
                    it[createAt] = Instant.now().epochSecond
                    it[createdBy] = createUser
                    it[repository] = structure.repository
                }
            }
        }
    }

    suspend fun deleteArtifact(artifact: DeleteArtifact) {
        suspendedTransaction {
            ArtifactTable.deleteWhere {
                (groupId eq artifact.groupId)
                    .and(artifactId eq artifact.artifactId)
                    .and(version eq artifact.version)
                    .and(repository eq artifact.repository)
            }
        }
    }
}