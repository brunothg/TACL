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

import alt.foss.brunothg.tacl.bl.filter.CallFilter
import alt.foss.brunothg.tacl.misc.ID
import kotlinx.coroutines.flow.Flow

/**
 * Repository for call filters.
 */
interface CallFilterRepository {

    /**
     * The list of call filters ordered by their order index.
     */
    val callFilters: Flow<List<CallFilter>>

    /**
     * Get a call filter by its id.
     *
     * @param id The id of the call filter.
     * @return A flow of the call filter.
     */
    fun getCallFilter(id: ID): Flow<CallFilter?>

    /**
     * Save (create or update) a call filter.
     *
     * @param callFilter The call filter to save.
     */
    suspend fun saveCallFilter(
        callFilter: CallFilter
    )

    /**
     * Delete a call filter.
     *
     * @param callFilter The call filter to delete.
     */
    suspend fun deleteCallFilter(
        callFilter: CallFilter
    )
}