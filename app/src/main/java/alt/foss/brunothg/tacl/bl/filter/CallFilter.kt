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
 *
 * @property orderIndex The order in which this filter will be applied.
 * @property name Short user given name
 * @property description Optional detail description
 * @property enabled Is the filter enabled/disabled
 * @property invertMatch If true the matchers result will be inverted
 * @property matcher The call matcher deciding if this filter will be applied to a specific call
 * @property action The action taking place if the filter will be applied to a specific call
 */
data class CallFilter(
    val id: ID = ID.uuid(),
    val orderIndex: Int,
    val name: String,
    val description: String? = null,
    val enabled: Boolean = true,
    val invertMatch: Boolean = false,
    val matcher: CallMatcher,
    val action: FilterAction,
) {

    companion object {

        /**
         * Apply this filter chain to a specific call.
         *
         * @param call The call to filter.
         * @return The resulting action with intermediates.
         */
        fun Collection<CallFilter>.filterCallWithIntermediate(
            call: Call,
        ): Pair<List<Pair<CallFilter, FilterAction>>, FilterAction> {
            return this
                .filter { it.enabled }
                .filter {
                    val match = it.matcher.appliesTo(call)
                    if (it.invertMatch) !match else match
                }
                .map { it to it.action }
                .runningReduce { acc, current ->
                    current.first to acc.second + current.second
                }
                .let { intermediates ->
                    intermediates to (
                            intermediates
                                .map { it.second }
                                .lastOrNull() ?: FilterAction.NOTHING
                            )
                }
        }

        /**
         * Apply this filter chain to a specific call.
         *
         * @param call The call to filter.
         * @return The resulting action.
         */
        fun Collection<CallFilter>.filterCall(
            call: Call,
        ): FilterAction {
            return this
                .filter { it.enabled }
                .filter {
                    val match = it.matcher.appliesTo(call)
                    if (it.invertMatch) !match else match
                }
                .fold(
                    FilterAction.NOTHING
                ) { acc, current ->
                    acc + current.action
                }
        }

    }

}
