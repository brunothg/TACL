/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.misc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Common ID type.
 *
 * @property value The string value of the ID.
 */
@Parcelize
@JvmInline
value class ID(val value: String) : Parcelable {
    companion object {
        /**
         * Generate a new UUID as an [ID].
         *
         * @return The new [ID].
         */
        fun uuid(): ID = ID(UUID.randomUUID().toString())
    }
}