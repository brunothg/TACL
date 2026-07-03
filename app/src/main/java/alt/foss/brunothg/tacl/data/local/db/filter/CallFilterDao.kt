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

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import alt.foss.brunothg.tacl.data.local.db.misc.CrudDao
import alt.foss.brunothg.tacl.misc.ID
import kotlinx.coroutines.flow.Flow

/**
 * DAO for call filters.
 */
@Dao
interface CallFilterDao : CrudDao<CallFilterEntity> {

    /**
     * Get a call filter by its ID.
     *
     * @param id The ID.
     * @return A flow of the call filter.
     */
    @Query("SELECT * FROM call_filters WHERE id = :id")
    fun getById(id: ID): Flow<CallFilterEntity?>

    /**
     * Get all call filters ordered by their order index.
     *
     * @return A flow of the list of call filters.
     */
    @Query("SELECT * FROM call_filters ORDER BY order_index ASC")
    fun getAll(): Flow<List<CallFilterEntity>>

    /**
     * Delete all call filters.
     * @param callFilters The filters to delete.
     */
    @Delete
    fun deleteAll(callFilters: List<CallFilterEntity>)

    /**
     * Delete all call filters.
     */
    @Query("DELETE FROM call_filters")
    fun deleteAll()

}