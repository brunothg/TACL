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

import alt.foss.brunothg.tacl.bl.Call

/**
 * Mapper between database entities and domain models for calls.
 */
object CallMapper {

    /**
     * Map [CallEmbeddable] to [Call].
     *
     * @return The [Call].
     */
    fun CallEmbeddable.toDomain(): Call {
        return Call(
            identifier = identifier,
            region = region,
            isEmergencyCall = isEmergencyCall,
        )
    }

    /**
     * Map [Call] to [CallEmbeddable].
     *
     * @return The [CallEmbeddable].
     */
    fun Call.toDb(): CallEmbeddable {
        return CallEmbeddable(
            identifier = identifier,
            region = region,
            isEmergencyCall = isEmergencyCall,
        )
    }

}