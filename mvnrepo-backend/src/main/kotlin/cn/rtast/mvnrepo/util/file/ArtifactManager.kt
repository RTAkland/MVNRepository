/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/19
 */


package cn.rtast.mvnrepo.util.file

import cn.rtast.mvnrepo.db.ArtifactTable
import cn.rtast.mvnrepo.entity.api.PackageStatistics
import cn.rtast.mvnrepo.util.suspendedTransaction
import org.jetbrains.exposed.sql.*

class ArtifactManager {

    suspend fun increaseDownloadCount(group: String, repository: String, artifactId: String) {
        suspendedTransaction {
            val ifExists = ArtifactTable.selectAll()
                .where {
                    (ArtifactTable.artifactId eq artifactId)
                        .and { ArtifactTable.repository eq repository }
                        .and { ArtifactTable.groupId eq group }
                }.singleOrNull()
            if (ifExists == null) {
                ArtifactTable.insert {
                    it[ArtifactTable.artifactId] = artifactId
                    it[groupId] = group
                    it[ArtifactTable.repository] = repository
                    it[downloadCount] = 0L
                }
            } else {
                ArtifactTable.update({
                    (ArtifactTable.groupId eq group)
                        .and { ArtifactTable.artifactId eq artifactId }
                        .and { ArtifactTable.repository eq repository }
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
            val result = ArtifactTable.selectAll()
                .where {
                    (ArtifactTable.artifactId eq artifactId)
                        .and { ArtifactTable.repository eq repository }
                        .and { ArtifactTable.groupId eq group }
                }.map {
                    PackageStatistics(
                        group, artifactId, repository,
                        it[ArtifactTable.downloadCount]
                    )
                }.firstOrNull()
            return@suspendedTransaction result
        }
    }
}