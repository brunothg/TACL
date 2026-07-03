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

import alt.foss.brunothg.tacl.bl.filter.CallFilter
import alt.foss.brunothg.tacl.bl.filter.FilterAction
import alt.foss.brunothg.tacl.data.local.db.filter.matchers.CallMatcherMapper.toDb
import alt.foss.brunothg.tacl.data.local.db.filter.matchers.CallMatcherMapper.toDomain

/**
 * Mapper between database entities and domain models for call filters.
 */
object CallFilterMapper {

    /**
     * Map [CallFilterEntity] to [CallFilter].
     *
     * @return The [CallFilter].
     */
    fun CallFilterEntity.toDomain(): CallFilter {
        return CallFilter(
            id = id,
            orderIndex = orderIndex,
            name = name,
            description = description,
            enabled = enabled,
            invertMatch = invertMatch,
            matcher = matcher.toDomain(),
            action = action.toDomain(),
        )
    }

    /**
     * Map [CallFilter] to [CallFilterEntity].
     *
     * @return The [CallFilterEntity].
     */
    fun CallFilter.toDb(): CallFilterEntity {
        return CallFilterEntity(
            id = id,
            orderIndex = orderIndex,
            name = name,
            description = description,
            enabled = enabled,
            invertMatch = invertMatch,
            matcher = matcher.toDb(),
            action = action.toDb(),
        )
    }

    /**
     * Map [FilterActionEmbeddable] to [FilterAction].
     *
     * @return The [FilterAction].
     */
    fun FilterActionEmbeddable.toDomain(): FilterAction {
        return FilterAction(
            block = block,
            ring = ring,
            notification = notification,
            callLog = callLog,
        )
    }

    /**
     * Map [FilterAction] to [FilterActionEmbeddable].
     *
     * @return The [FilterActionEmbeddable].
     */
    fun FilterAction.toDb(): FilterActionEmbeddable {
        return FilterActionEmbeddable(
            block = block,
            ring = ring,
            notification = notification,
            callLog = callLog,
        )
    }

}