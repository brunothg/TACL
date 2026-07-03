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

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import alt.foss.brunothg.tacl.data.local.db.filter.matchers.CallMatcherDto
import alt.foss.brunothg.tacl.misc.ID

/**
 * Database entity for a call filter.
 *
 * @property id The unique identifier of the filter.
 * @property orderIndex The order in which the filter is applied.
 * @property name The name of the filter.
 * @property description An optional description of the filter.
 * @property enabled True if the filter is enabled.
 * @property invertMatch True if the match result should be inverted.
 * @property matcher The matcher used to decide if the filter applies.
 * @property action The action to take if the filter applies.
 */
@Entity(
    tableName = "call_filters",
    indices = [
        Index(
            value = ["order_index"],
            orders = [Index.Order.ASC],
            unique = true
        )
    ]
)
data class CallFilterEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: ID,
    @ColumnInfo(name = "order_index") val orderIndex: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "enabled") val enabled: Boolean,
    @ColumnInfo(name = "invert_match") val invertMatch: Boolean,

    @ColumnInfo(name = "matcher") val matcher: CallMatcherDto,
    @Embedded(prefix = "action_") val action: FilterActionEmbeddable,
)
