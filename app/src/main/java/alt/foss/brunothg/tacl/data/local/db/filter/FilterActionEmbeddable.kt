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
import alt.foss.brunothg.tacl.bl.filter.BlockType

/**
 * Embeddable entity for filter actions.
 *
 * @property block The block type.
 * @property ring True if it should ring.
 * @property notification True if it should notify.
 * @property callLog True if it should be logged.
 */
data class FilterActionEmbeddable(
    @ColumnInfo(name = "block") val block: BlockType?,
    @ColumnInfo(name = "ring") val ring: Boolean?,
    @ColumnInfo(name = "notification") val notification: Boolean?,
    @ColumnInfo(name = "call_log") val callLog: Boolean?,
)
