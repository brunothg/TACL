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
 * Specifies the way a call is possibly blocked.
 */
enum class BlockType(
    val id: String
) {
    /**
     * Ignore call, but do not hang up
     */
    IGNORE(id = "ignore"),

    /**
     * Ignore and hangup
     */
    HANGUP(id = "hangup"),
}