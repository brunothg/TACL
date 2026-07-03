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
 */
data class FilterAction(
    /**
     * If present in which way should the call be blocked
     */
    val block: BlockType? = null,

    /**
     * Should the phone ring
     */
    val ring: Boolean? = null,

    /**
     * Should there be a notification for the call
     */
    val notification: Boolean? = null,

    /**
     * Should the call be present in the call log
     */
    val callLog: Boolean? = null,
) {

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