/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/19
 */


package cn.rtast.mvnrepo.util.file

import cn.rtast.mvnrepo.db.ArtifactDownloadCountTable
import cn.rtast.mvnrepo.db.ArtifactTable
import cn.rtast.mvnrepo.entity.PackageStructure
import cn.rtast.mvnrepo.entity.api.PackageStatistics
import cn.rtast.mvnrepo.entity.db.ArtifactSearchResult
import cn.rtast.mvnrepo.util.suspendedTransaction
import org.jetbrains.exposed.sql.*
import java.time.Instant

class ArtifactManager {

    suspend fun increaseDownloadCount(group: String, repository: String, artifactId: String) {
        suspendedTransaction {
            val ifExists = ArtifactDownloadCountTable.selectAll()
                .where {
                    (ArtifactDownloadCountTable.artifactId eq artifactId)
                        .and { ArtifactDownloadCountTable.repository eq repository }
                        .and { ArtifactDownloadCountTable.groupId eq group }
                }.singleOrNull()
            if (ifExists == null) {
                ArtifactDownloadCountTable.insert {
                    it[ArtifactDownloadCountTable.artifactId] = artifactId
                    it[groupId] = group
                    it[ArtifactDownloadCountTable.repository] = repository
                    it[downloadCount] = 0L
                }
            } else {
                ArtifactDownloadCountTable.update({
                    (ArtifactDownloadCountTable.groupId eq group)
                        .and { ArtifactDownloadCountTable.artifactId eq artifactId }
                        .and { ArtifactDownloadCountTable.repository eq repository }
                }) {
                    with(SqlExpressionBuilder) {
                        it.update(downloadCount, downloadCount + 1)
                    }
                }
            }
        }
    }

    suspend fun getDownloadCount(group: String, repository: String, artifactId: String): PackageStatistics? {
        return suspendedTransaction {
            val result = ArtifactDownloadCountTable.selectAll()
                .where {
                    (ArtifactDownloadCountTable.artifactId eq artifactId)
                        .and { ArtifactDownloadCountTable.repository eq repository }
                        .and { ArtifactDownloadCountTable.groupId eq group }
                }.map {
                    PackageStatistics(
                        group, artifactId, repository,
                        it[ArtifactDownloadCountTable.downloadCount]
                    )
                }.firstOrNull()
            return@suspendedTransaction result
        }
    }

    suspend fun addOrUpdateArtifact(packageStructure: PackageStructure, createBy: String) {
        suspendedTransaction {
            val ifExists = ArtifactTable.selectAll()
                .where {
                    (ArtifactTable.artifactId eq packageStructure.artifactId)
                        .and { ArtifactTable.repository eq packageStructure.repository }
                        .and { ArtifactTable.groupId eq packageStructure.artifactGroup }
                        .and { ArtifactTable.version eq packageStructure.artifactVersion }
                }.singleOrNull()
            if (ifExists == null) {
                ArtifactTable.insert {
                    it[groupId] = packageStructure.artifactGroup
                    it[version] = packageStructure.artifactVersion
                    it[repository] = packageStructure.repository
                    it[artifactId] = packageStructure.artifactId
                    it[createdBy] = createBy
                    it[lastUpdated] = Instant.now().epochSecond
                }
            } else {
                ArtifactTable.update({
                    (ArtifactTable.artifactId eq packageStructure.artifactId)
                        .and { ArtifactTable.repository eq packageStructure.repository }
                        .and { ArtifactTable.groupId eq packageStructure.artifactGroup }
                        .and { ArtifactTable.version eq packageStructure.artifactVersion }
                        .and { ArtifactTable.createdBy eq createBy }
                }) {
                    it[artifactId] = packageStructure.artifactId
                    it[version] = packageStructure.artifactVersion
                    it[groupId] = packageStructure.artifactGroup
                    it[repository] = packageStructure.repository
                    it[createdBy] = createBy
                    it[lastUpdated] = Instant.now().epochSecond
                }
            }
        }
    }

    suspend fun searchArtifact(artifactId: String): List<ArtifactSearchResult> {
        return suspendedTransaction {
            val result = ArtifactTable.selectAll().where {
                ArtifactTable.artifactId like "%$artifactId%"
            }.map {
                ArtifactSearchResult(
                    it[ArtifactTable.groupId],
                    it[ArtifactTable.artifactId],
                    it[ArtifactTable.repository],
                    it[ArtifactTable.createdBy],
                    it[ArtifactTable.lastUpdated],
                    it[ArtifactTable.version]
                )
            }
            return@suspendedTransaction result
        }
    }
}