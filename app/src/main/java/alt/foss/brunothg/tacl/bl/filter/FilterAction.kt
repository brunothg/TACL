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

/**
 * Filter action defining the way a call should be handled.
 * Optional properties will result in system defaults if not set or set to null.
 *
 * @property block If present in which way should the call be blocked
 * @property ring Should the phone ring
 * @property notification Should there be a notification for the call
 * @property callLog Should the call be present in the call log
 */
data class FilterAction(
    val block: BlockType? = null,
    val ring: Boolean? = null,
    val notification: Boolean? = null,
    val callLog: Boolean? = null,
) {

    companion object {

        /**
         * Explicit filter action for doing nothing.
         * Not necessary the same as default constructor`FilterAction()`.
         */
        val NOTHING = FilterAction()
    }

    /**
     * Create the merged action of this and the other action.
     * The result uses this value if not null or else the others.
     *
     * @param other the other action to merge with
     */
    operator fun plus(other: FilterAction): FilterAction {
        return FilterAction(
            block = this.block ?: other.block,
            ring = this.ring ?: other.ring,
            notification = this.notification ?: other.notification,
            callLog = this.callLog ?: other.callLog,
        )
    }

}