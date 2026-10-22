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
import alt.foss.brunothg.tacl.data.local.db.call.CallMapper.toDb
import alt.foss.brunothg.tacl.data.local.db.call.CallMapper.toDomain
import alt.foss.brunothg.tacl.data.local.db.filter.CallFilterMapper.toDb
import alt.foss.brunothg.tacl.data.local.db.filter.CallFilterMapper.toDomain

/**
 * Mapper between database entities and domain models for the filter log.
 */
object FilterLogMapper {

    /**
     * Map [FilterLogEntryEntity] to [FilterLogEntry].
     *
     * @return The [FilterLogEntry].
     */
    fun FilterLogEntryEntity.toDomain(): FilterLogEntry {
        return FilterLogEntry(
            id = id,
            timestamp = timestamp,
            call = call.toDomain(),
            action = action.toDomain(),
        )
    }

    /**
     * Map [FilterLogEntry] to [FilterLogEntryEntity].
     *
     * @return The [FilterLogEntryEntity].
     */
    fun FilterLogEntry.toDb(): FilterLogEntryEntity {
        return FilterLogEntryEntity(
            id = id,
            timestamp = timestamp,
            call = call.toDb(),
            action = action.toDb(),
        )
    }

}