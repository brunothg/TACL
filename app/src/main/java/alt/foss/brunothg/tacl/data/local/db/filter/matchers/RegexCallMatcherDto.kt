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

import alt.foss.brunothg.tacl.data.local.db.misc.serializers.RegexSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data transfer object for regex call matchers.
 *
 * @property regex The regex pattern.
 */
@Serializable
@SerialName("regex_call_matcher")
data class RegexCallMatcherDto(

    @SerialName("match_pattern")
    @Serializable(with = RegexSerializer::class)
    val regex: Regex,

    ) : CallMatcherDto