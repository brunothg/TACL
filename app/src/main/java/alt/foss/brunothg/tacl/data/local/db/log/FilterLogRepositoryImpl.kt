/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.data.local.db.log

import alt.foss.brunothg.tacl.bl.log.FilterLogEntry
import alt.foss.brunothg.tacl.bl.repository.FilterLogRepository
import alt.foss.brunothg.tacl.data.local.db.AppDatabase
import alt.foss.brunothg.tacl.data.local.db.log.FilterLogMapper.toDb
import alt.foss.brunothg.tacl.data.local.db.log.FilterLogMapper.toDomain
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import androidx.room.withTransaction
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of [FilterLogRepository] using Room database.
 *
 * @property appDatabase The database.
 * @property filterLogDao The DAO for the call filter log.
 */
class FilterLogRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val filterLogDao: FilterLogDao,
) : FilterLogRepository {

    override fun getLogEntriesPaged(
        config: PagingConfig
    ) = Pager(
        config = config,
    ) {
        filterLogDao.getAllPaged()
    }.flow
        .map {
            it.map { entity ->
                entity.toDomain()
            }
        }

    override suspend fun saveAndPruneLogEntries(
        logEntries: List<FilterLogEntry>,
        limit: Int?
    ) {
        appDatabase.withTransaction {
            filterLogDao.upsertAll(
                logEntries.map { it.toDb() }
            )
            limit?.also {
                filterLogDao.pruneOld(it)
            }
        }
    }

    override suspend fun clearLog() {
        filterLogDao.deleteAll()
    }

}
