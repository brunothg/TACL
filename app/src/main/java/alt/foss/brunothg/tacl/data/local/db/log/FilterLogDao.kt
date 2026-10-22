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

import alt.foss.brunothg.tacl.data.local.db.misc.CrudDao
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query

/**
 * DAO for the call filter log.
 */
@Dao
interface FilterLogDao : CrudDao<FilterLogEntryEntity> {

    /**
     * Get all log entries ordered by their timestamp (least first).
     *
     * @return The paged log entries.
     */
    @Query("SELECT * FROM filter_log_entries ORDER BY timestamp DESC")
    fun getAllPaged(): PagingSource<Int, FilterLogEntryEntity>

    /**
     * Delete all log entries.
     * Clear the whole log.
     */
    @Query("DELETE FROM filter_log_entries")
    suspend fun deleteAll()

    /**
     * Delete all log entries over the limit.
     * Delete the oldest entries first.
     */
    @Query("""
        DELETE FROM filter_log_entries 
        WHERE (timestamp, id) < (
            SELECT timestamp, id 
            FROM filter_log_entries 
            ORDER BY timestamp DESC, id DESC
            LIMIT 1 OFFSET :limit - 1
        )
    """)
    suspend fun pruneOld(limit: Int)
}