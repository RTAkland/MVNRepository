/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/19
 */


package cn.rtast.mvnrepo.util.file

import cn.rtast.mvnrepo.db.ArtifactDownloadCountTable
import cn.rtast.mvnrepo.db.ArtifactTable
import cn.rtast.mvnrepo.db.ArtifactTable.artifactId
import cn.rtast.mvnrepo.db.ArtifactTable.createdBy
import cn.rtast.mvnrepo.db.ArtifactTable.groupId
import cn.rtast.mvnrepo.db.ArtifactTable.lastUpdated
import cn.rtast.mvnrepo.db.ArtifactTable.repository
import cn.rtast.mvnrepo.db.ArtifactTable.version
import cn.rtast.mvnrepo.entity.PackageStructure
import cn.rtast.mvnrepo.entity.api.PackageStatistics
import cn.rtast.mvnrepo.entity.db.DBSearchResult
import cn.rtast.mvnrepo.enums.SearchType
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
            ArtifactTable.upsert(version, repository, groupId, artifactId, onUpdate = { stat ->
                stat[groupId] = packageStructure.artifactGroup
                stat[artifactId] = packageStructure.artifactId
                stat[repository] = packageStructure.repository
                stat[createdBy] = createBy
                stat[lastUpdated] = Instant.now().epochSecond
            }) {
                it[groupId] = packageStructure.artifactGroup
                it[artifactId] = packageStructure.artifactId
                it[repository] = packageStructure.repository
                it[version] = packageStructure.artifactVersion
                it[createdBy] = createBy
                it[lastUpdated] = Instant.now().epochSecond
            }
        }
    }

    suspend fun searchByArtifact(keyword: String, searchType: SearchType): List<DBSearchResult> {
        return suspendedTransaction {
            val result = when (searchType) {
                SearchType.ArtifactId -> ArtifactTable.selectAll().where {
                    artifactId like "%$keyword%"
                }

                SearchType.GroupId -> ArtifactTable.selectAll().where {
                    groupId like "%$keyword%"
                }

                SearchType.Version -> ArtifactTable.selectAll().where {
                    version like "%$keyword%"
                }

                SearchType.Repository -> ArtifactTable.selectAll().where {
                    repository like "%$keyword%"
                }

                SearchType.Author -> ArtifactTable.selectAll().where {
                    createdBy like "%$keyword%"
                }
            }.map {
                DBSearchResult(
                    it[groupId],
                    it[artifactId],
                    it[repository],
                    it[createdBy],
                    it[lastUpdated],
                    it[version]
                )
            }
            return@suspendedTransaction result
        }
    }
}