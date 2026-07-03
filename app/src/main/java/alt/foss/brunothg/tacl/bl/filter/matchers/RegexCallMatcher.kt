/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.bl.filter.matchers

import alt.foss.brunothg.tacl.bl.Call

/**
 * Call matcher using a regular expression.
 *
 * @property regex The regular expression to match against the phone number.
 */
data class RegexCallMatcher(
    val regex: Regex,
) : CallMatcher {
    override fun appliesTo(call: Call): Boolean {
        return regex.matches(call.phoneNumber)
    }
}
