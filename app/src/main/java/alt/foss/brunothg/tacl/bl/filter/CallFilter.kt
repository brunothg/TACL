/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.bl.filter

import alt.foss.brunothg.tacl.bl.Call
import alt.foss.brunothg.tacl.bl.filter.matchers.CallMatcher
import alt.foss.brunothg.tacl.misc.ID

/**
 * A call filter defining if a call is filtered and if so how to handle it.
 */
data class CallFilter(
    val id: ID = ID.uuid(),

    /**
     * The order in which this filter will be applied.
     */
    val orderIndex: Int, // TODO ensure order index >= 0

    /**
     * Short user given name
     */
    val name: String, // TODO ensure name not empty, trimmed

    /**
     * Optional detail description
     */
    val description: String? = null, // TODO ensure trimmed

    /**
     * Is the filter enabled/disabled
     */
    val enabled: Boolean = true,

    /**
     * If true the matchers result will be inverted
     */
    val invertMatch: Boolean = false,

    /**
     * The call matcher deciding if this filter will be applied to a specific call
     */
    val matcher: CallMatcher,

    /**
     * The action taking place if the filter will be applied to a specific call
     */
    val action: FilterAction,
) {

    companion object {

        /**
         * Apply this filter chain to a specific call.
         *
         * @param call The call to filter.
         * @return The resulting action.
         */
        fun Collection<CallFilter>.filterCall(call: Call): FilterAction? {
            return this
                .filter { it.enabled }
                .filter {
                    val match = it.matcher.appliesTo(call)
                    if (it.invertMatch) !match else match
                }
                .map { it to it.action }
                .reduceOrNull { acc, current ->
                    // val accFilter = acc.first
                    val accAction = acc.second
                    val currentFilter = current.first
                    val currentAction = current.second

                    currentFilter to (accAction + currentAction)
                }?.second
        }

    }

}
