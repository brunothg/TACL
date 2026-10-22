/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.bl.log

import alt.foss.brunothg.tacl.bl.Call
import alt.foss.brunothg.tacl.bl.filter.FilterAction
import alt.foss.brunothg.tacl.misc.ID
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * A log entry recording the filter result for a call.
 *
 * @property timestamp Timestamp of the log entry.
 * @property call The call that was filtered.
 * @property action The filter action that was taken.
 */
data class FilterLogEntry(
    val id: ID = ID.uuid(),
    val timestamp: Instant = Clock.System.now(),
    val call: Call,
    val action: FilterAction,
)