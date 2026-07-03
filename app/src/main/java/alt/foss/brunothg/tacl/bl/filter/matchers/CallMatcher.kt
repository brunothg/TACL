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
 * Call matcher for deciding if a filter should be applied to a specific call.
 */
sealed interface CallMatcher {

    /**
     * Decide if a filter should be applied to the specified call
     *
     * @param call The call
     */
     fun appliesTo(call: Call): Boolean

}