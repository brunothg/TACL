/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.data.local.db.filter.matchers

import alt.foss.brunothg.tacl.bl.filter.matchers.CallMatcher
import alt.foss.brunothg.tacl.bl.filter.matchers.RegexCallMatcher

/**
 * Mapper between database DTOs and domain models for call matchers.
 */
object CallMatcherMapper {

    /**
     * Map [RegexCallMatcherDto] to [RegexCallMatcher].
     *
     * @return The [RegexCallMatcher].
     */
    fun RegexCallMatcherDto.toDomain(): RegexCallMatcher {
        return RegexCallMatcher(regex = regex)
    }

    /**
     * Map [RegexCallMatcher] to [RegexCallMatcherDto].
     *
     * @return The [RegexCallMatcherDto].
     */
    fun RegexCallMatcher.toDb(): RegexCallMatcherDto {
        return RegexCallMatcherDto(regex = regex)
    }

    /**
     * Map [CallMatcherDto] to [CallMatcher].
     *
     * @return The [CallMatcher].
     */
    fun CallMatcherDto.toDomain(): CallMatcher {
        return when (this) {
            is RegexCallMatcherDto -> this.toDomain()
        }
    }

    /**
     * Map [CallMatcher] to [CallMatcherDto].
     *
     * @return The [CallMatcherDto].
     */
    fun CallMatcher.toDb(): CallMatcherDto {
        return when (this) {
            is RegexCallMatcher -> this.toDb()
        }
    }

}