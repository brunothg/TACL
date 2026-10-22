/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.data.local.db.call

import androidx.room.ColumnInfo
import alt.foss.brunothg.tacl.bl.filter.BlockType
import java.net.URI

/**
 * Embeddable entity for calls.
 *
 * @property identifier The [URI] of the call (tel://&lt;phone number&gt; or similar).
 * @property region The region of the call.
 * @property isEmergencyCall Whether the call is an emergency call or not.
 */
data class CallEmbeddable(
    @ColumnInfo(name = "identifier") val identifier: URI,
    @ColumnInfo(name = "region") val region: String?,
    @ColumnInfo(name = "is_emergency_call") val isEmergencyCall: Boolean,
)
