/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.bl.repository

import alt.foss.brunothg.tacl.bl.log.FilterLogEntry
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

/**
 * Repository for the call filter log.
 */
interface FilterLogRepository {

    /**
     * The paged log entries ordered by their timestamp (least first).
     */
    fun getLogEntriesPaged(
        config: PagingConfig = PagingConfig(
            pageSize = 100,
        )
    ): Flow<PagingData<FilterLogEntry>>

    /**
     * Save (create or update) a log entry and ensure there are no more than [limit] log entries.
     *
     * @param logEntries The log entries to save.
     * @param limit The maximum number of log entries.
     */
    suspend fun saveAndPruneLogEntries(
        logEntries: List<FilterLogEntry>,
        limit: Int?,
    )

    /**
     * Clear the whole log.
     */
    suspend fun clearLog()
}