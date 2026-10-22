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

import alt.foss.brunothg.tacl.data.local.db.call.CallEmbeddable
import alt.foss.brunothg.tacl.data.local.db.filter.FilterActionEmbeddable
import alt.foss.brunothg.tacl.misc.ID
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.time.Instant

/**
 * Database entity for a filter log entry.
 *
 * @property id The unique identifier of the filter.
 * @property timestamp The timestamp of the log entry.
 * @property call The call that was filtered.
 * @property action The action that was performed.
 */
@Entity(
    tableName = "filter_log_entries",
    indices = [
        Index(
            value = ["timestamp"],
            orders = [Index.Order.DESC],
        )
    ]
)
data class FilterLogEntryEntity (
    @PrimaryKey @ColumnInfo(name = "id") val id: ID,
    @ColumnInfo(name = "timestamp") val timestamp: Instant,
    @Embedded(prefix = "call_") val call: CallEmbeddable,
    @Embedded(prefix = "action_") val action: FilterActionEmbeddable,
)