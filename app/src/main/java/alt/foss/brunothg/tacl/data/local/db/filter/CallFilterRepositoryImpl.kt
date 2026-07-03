/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.data.local.db.filter

import androidx.room.withTransaction
import alt.foss.brunothg.tacl.bl.filter.CallFilter
import alt.foss.brunothg.tacl.bl.repository.CallFilterRepository
import alt.foss.brunothg.tacl.data.local.db.AppDatabase
import alt.foss.brunothg.tacl.data.local.db.filter.CallFilterMapper.toDb
import alt.foss.brunothg.tacl.data.local.db.filter.CallFilterMapper.toDomain
import alt.foss.brunothg.tacl.misc.ID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.min

/**
 * Implementation of [CallFilterRepository] using Room database.
 *
 * @property appDatabase The database.
 * @property callFilterDao The DAO for call filters.
 */
class CallFilterRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val callFilterDao: CallFilterDao,
) : CallFilterRepository {

    override val callFilters = callFilterDao.getAll().map { callFilters ->
        callFilters.map { callFilter ->
            callFilter.toDomain()
        }
    }

    override fun getCallFilter(id: ID): Flow<CallFilter?> = callFilterDao.getById(id).map {
        it?.toDomain()
    }

    override suspend fun saveCallFilter(callFilter: CallFilter) {
        appDatabase.withTransaction {
            val all = callFilterDao.getAll().first()
            val current = all.find { it.id == callFilter.id }
            val appendOrderIndex = all.maxOfOrNull { it.orderIndex + 1 } ?: 0
            val isReorder = all.isNotEmpty() // Not empty
                    && current?.orderIndex != callFilter.orderIndex // Not same as before
                    && listOfNotNull(callFilter.orderIndex, current?.orderIndex)
                .any { it < appendOrderIndex }// Inside existing range

            if (!isReorder) {
                // Modify/Create without reordering
                val normalizedOrderIndex = min(callFilter.orderIndex, appendOrderIndex)
                callFilterDao.upsert(
                    callFilter
                        .copy(orderIndex = normalizedOrderIndex)
                        .toDb()
                )
            } else {
                // Order and rewrite list
                callFilterDao.deleteAll()
                callFilterDao.upsertAll(
                    (
                            all
                                .filter { it != current }
                                .mapIndexed { index, it ->
                                    if (index >= callFilter.orderIndex) {
                                        it.copy(orderIndex = index + 1)
                                    } else {
                                        it
                                    }
                                } + callFilter.toDb()
                            )
                        .sortedBy { it.orderIndex }
                        .mapIndexed { index, it ->
                            it.copy(orderIndex = index)
                        }
                )
            }
        }
    }

    override suspend fun deleteCallFilter(callFilter: CallFilter) {
        appDatabase.withTransaction {
            val all = callFilterDao.getAll().first()
            val current = all.find { it.id == callFilter.id }
            val reorderList = all
                .filter {
                    current != null && it != current && it.orderIndex > current.orderIndex
                }
                .map {
                    it.copy(orderIndex = it.orderIndex - 1)
                }

            callFilterDao.deleteAll((reorderList + current).filterNotNull())
            callFilterDao.upsertAll(reorderList)
        }
    }

}
